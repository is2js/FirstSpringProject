### 세팅

start.spring.io

- gradle project
- java
- 추천버전
- **Artifact (프로젝트명) : firstproject**
- jar
- java버전

- dependencies

    - sptring `web`
    - `h2`
    - `mustache`
    - `jpa`

- 다운로드후, 인텔리제이로 zip속 폴더를 열어버리면 됨.
- mustache 플러그인을 추가설치해야, intellij가 인식해줌.

- CRUD 동작으로 인한 SQL쿼리문 보기를 위한

1. jpa 로깅 설정
    1. application.properties에서 SQL을  `디버그` 로그레벨로 설정 + 쿼리 이쁘게 보여주도록 설정(format형태로 출력) + 물음표 바인드변수 출력
        - `logging.level.org.hibernate.SQL = DEBUG`
        - `spring.jpa.properties.hibernate.format_sql=true`
        - `logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE`
    2. jdbc 내장 mem db 고정 url설정
        - `spring.datasource.generate-unique-name=false`
        - `spring.datasource.url=jdbc:h2:mem:testdb`

### RestAPI에 서비스계층 추가하기

- RestController <-> Repository 사이에 Service
  ![image-20220427225358403](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427225358403.png)

1. 서비스 개념
    1. 컨트롤러와 리파지토리사이에 존재하는 계층으로서 **처리업무들의 순서를 총괄**
        - 순서대로 업무처리할 때, 필요한 재료를 repository로 부터 받아온다.
    2. 비유
        1. client(json): 손님
        2. RestController: 웨이터 (손님의 요청/응답 전달)
        4. Service: 총괄 쉐프(업무들 순서 총괄, 트랜잭션 단위로 관리, repository에게 재료가져오라고 일 시킴)
        5. Repository: 보조 쉐프(재료 갖다줌)
    3. 트랜잭션과 롤백
        1. **서비스의 업무들 처리는 Transaction단위로 시행된다.**
        2. `트랜잭션`: `모두 성공`되어야하는 일련의 과정
            - 하나라도 실패시 처음으로 돌리는 것: `롤백`

    4. 기존 코드 문제점
        - **Controller는 웨이터이지만, 현재는 `웨이터`(요청/응답 전달)와 `쉐프`(업무들 순서총괄하며 트랜잭션 단위로 관리, repository에게 재료가져오라고 일 시킴)의 2가지 일을 동시에
          하고 있다.**
          ![image-20220427225925273](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427225925273.png)
          ![image-20220427225955248](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427225955248.png)
        - 일반적으로 웹서비스는, controller와 repository사이에 service계층을 두어서 역할을 분업화 한다.

### service 도입하기 (18)

1. 컨트롤러 내용을 전체 주석처리하고, Repository 주입해서 쓰던 것을 -> Controller에tj Service 주입하도록 작성한다 -> service패키지를 만들고 거기에 해당 서비스class를
   생성한다.
    1. 빈에 등록되는 3가지 Component 중 1개로서 `@Service` 어노테이션 달아주기
    2. **@Component애노테이션 자체가 `부트에 빈 객체 생성`하는 것이고 -> @Autowired주입은 `@어노태이션으로 미리 생성된 빈 객체를 땡겨와서 주입`**
2. Service에서 Repository를 주입해서 쓰도록 하게 한다.
3. 이제 Controller의 주석을 하나씩 풀면서, `controller내 요청/응답을 제외한 코드` -> `service에게 시키기`로 코드를 리팩토링 하면 된다.
    1. GET 전체 조회
        - 기존 repo한테서 데이터 다받아옴 -> service한테 받아오도록 수정
        - **데이터 조회 controller의 경우 ResponseEntity를 바로 안쓰고, `List<Entity>`나 `Entity`로 반환을 유지했던 이유가.. 여기에...**
            - service가 할일로서, entity를 반환해줘야하니까.. controller에서도 그냥 entity반환으로 뒀구나... service도입시 그대로 집어넣을려구
        - service에게 시키는 메서드 전체조회: `.index()` / 개별조회: `.show()`
        - 주석푼 내용을 리팩토링 완료할때마다 api test하기
    2. POST 생성
        - GET조회와 다르게, id외에 데이터를 dto로 받아온 상태에서 **웨이터는 service(쉐프)에게 파라미터로 받은 것들을(`dto`)를 그대로 건네준다.**
        - **생성후 응답entity가 null이 아니면 ? good 그렇지 않으면 :bad로 응답하자**
            - good시 body에다가 잘 만들어진 데이터를 싣어서 보내면된다.
            - **그래야 response Body에서 id가 부여된 데이터를 확인할 수 있다.**
        - **dto는 service에서 받은 뒤, service내부에서 toEntity()로 변환한다.**
        ```java
        return (created != null) ?
            ResponseEntity.status(HttpStatus.OK).body(created) :
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        ```
        - create관련 빠진 로직 -> update/delete의 `조회 -> 잘못된요청 검증 -> 처리`처럼, create도 잘못된요청 `created != null? good: bad 이전`에
            - service에서 created <- **잘못된 create 요청을 null로 반환해줘야 created !=null확인으로 good/bad나눠서 응답된다.**
            - **`create의 잘못된요청(created자리에 null반환)` = id가 없이 와야, db에서 생성후 id를 배정한다! id를 포함한 데이터가 넘어온다면 error**
                - `id는 db가 생성하니 request body에 id가 들어있다 != null이다. -> 잘못된요청`으로서 -> entity자리에 null반환
                - **create시 들어오는 dto -> toEntity했는데 그 때 Entity안에 id가 있는 경우(!=null)**
    3. PATCH 수정
        - **웨이터는 service(쉐프)에게 파라미터로 받은 것들을(`id`, `dto`)를 그대로 건네준다.**
        - **웨이터는 받아온 `응답entity를 기준`으로 null아니면 ? good : 그렇지않으면 bad; 로 응답**해주면 된다.
        - controller에 코드가 너무 많았었다 -> 복붙해서 가져가서 내부에서 수정하면서 처리한다.
        - service에 복붙한 코드 중에 웨이터의 역할을 밖으로 뺀다. -> **service에서는 `잘못된 요청은 null반환`만 하면, controller가 null비교로 good/bad를 가른다.**
        - 다 작성하고 확인은 역시 apiTest(Talend)에서 한다. 요청주소를 모르겠으면 mappingmethod주소를 보고 하자.
            - URL에 개별id + requestBody에도 id + 수정할 데이터만(patch)
    4. DELETE 삭제
        - **삭제 성공시에는 삭제된 데이터를 service에서 응답은 해줘야한다. -> 응답entity가 아니라 조회된 entity `target`을 controller로 응답한다**
            - repository.delete(target)는 사실 응답entity가 없다 -> 삭제된 데이터를 응답해줘야한다면, 조회entity target을 응답해주자.
        - **controller에서는 삭제 성공시에는 200번대 `NO_CONTNENT`로.. 보내준다** -> 204가 응답된다!

### 트랜잭션 테스트(18 중간) by POST

1. 개념
    1. 트랜잭션: 모두 성공해야하는 일련의 과정 -> 하나라도 실패시 롤백
    2. 서비스: 업무순서를 총괄하며 트랜잭션 단위로 관리
2. 트랜잭선 테스트 실습
    1. TalendAPI에서 먼저 빨간요청
       ![image-20220428120629631](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220428120629631.png)
        - POST `api/transacton-test`이라는 URL로 POST(생성) 요청을 StringJson List로 해볼 것이다.
        - StringJsonList
            ```json
            [
              {
              "title": "시간 예약",
              "content":"11111"
              },
              {
              "title": "테이블 지정",
              "content":"22222"
              },
              {
              "title": "메뉴 선택",
              "content":"33333"
              }
            ]
            ```
    2. postmapping method in controller 작성
        1. jsonList를 던지는 경우 **`@RequestBody List<Dto> dtos`**로 받으면 된다.!!
    3. service(여러업무처리를 트랜잭션단위로)에서 여러데이터 생성중에 예외 발생 시켜보기
        1. dto묶음을 -> entity묶음으로 변환 by stream
        2. entity묶음을 -> db에 저장 by for문
        3. 강제로 예외 만들어보기 for transaction by repository findById를 음수 `-1L`로 중간에 에외
            - 트랜잭션 아니라면... 1,2번은 시행된 상태다? -> post던져보면, 2번에서 insert문 실행 -> DB에 데이터가 들어가버림
            - 뭐야.. 중간에 실패햇는데 db에 들어가있으면 안된다. -> 트랜잭션단위로 만들어야한다.
        4. 결과값반환
    4. **service의 메서드마다 `@Transactional`을 달아주면 된다.**
       - 중간에 에외발생시 db관련 동작도 다 롤백이 된다.

### 댓글 모음

#### PATCH

질문이 있는데요, Article Entity 클래스 내에 title 및 content 여부에 대한 처리를 patch함수를 등록하여 처리했는데, 해당 로직이 왜 Entity클래스에 적합한지 알 수 있을까요?
제가 생각하기엔 서비스 레이어에서 구현 되어야 하지 않을까 싶어 문의 드립니다.
해당 내용을 다룬 강의가 몇번이었는지 못 찾겠어서 여기 질문 남깁니다!

홍팍

비즈니스 로직을
서비스 레이어에서 따로 떼어내면
얻어지는 이점이 몇 가지 있습니다.

1. 가독성 증가
   비즈니스 로직이
   서비스 레이어에서 제거되므로
   서비스 코드가 더욱 간결해집니다.

2. 응집도 증가
   비즈니스 로직을
   관련 엔티티 내부에서 처리하게 되어
   프로그램의 응집도가 증가합니다.

그 결과
비즈니스 로직의 요구 사항 변경에도
유연하게 대처할 수 있습니다.
(엔티티 메소드만 변경하면
이를 사용하는
다양한 서비스 레이어에 동시 반영 )

3. JPA의 더티 체킹 활용성 증가
   JPA는 엔티티의 값의 변경을
   알아서 감지 및 DB로 반영 시켜주는데,
   도메인 모델 패턴은 이를 활용하기에 좋습니다.

구글링 키워드:
"트랜잭션 스크립트 패턴 vs 도메인 모델 패턴"

PS.
서비스 레이어에서
비즈니스 로직을 처리하게 좋은 경우도 있으니
상황에 맞게 쓰시면 되겠습니다

좋은 질문 감사합니다

#### REST
===

1. RestController 기반 프로그래밍이 트렌드임? true
   ===
   일반적으로 웹 개발은
   프론트 엔드(보이는쪽-클라이언트) 개발과
   백 엔드(안보이는쪽-서버) 개발로 나뉘는데,

백엔드 개발은 REST API로
프론트 엔드 개발은
각 클라이언트의 언어(또는 프레임워크)로 개발됩니다.

백엔드 개발자는 REST API만 개발
프론트 개발자는 화면 개발만 하여
분업화 하는거죠.

===

2. JSON만 있으면 화면은 어떻게 보여줌?
   ===
   화면이 아예 없다는 이야기는 아닙니다.
   전달하려 했던 핵심은,
   화면과 데이터가 구분되어 개발된다는 것인데요.

웹 페이지의 경우,
---
프론트 엔드 개발자가
HTML/CSS/JS로
(또는 리액트JS, 뷰JS 등의 프레임워크)
화면을 만들면

데이터는
REST API를 호출하여 가져온다
는 것이 포인트가 되겠습니다.

추가로
검색엔진 노출이 필요한 페이지와
그렇지 않은 페이지,
빠른 화면을 보여줘야하는 경우와
빠른 반응이 필요한 경우 등에 따라

어느 부분은 서버에서 화면을 만들고,
특정 부분은 클라이언트에서 화면을
만들기도 합니다.

좋은 질문 감사합니다 :)
