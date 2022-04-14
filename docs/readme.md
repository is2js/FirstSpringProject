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

### 강의

2. view template -> 화면을 담당하는 기술, 웹페이지html를 하나의 틀로 만들고 + 변수를 삽입할 수 있게 하여 -> 변수의 값에 따라 웹페이지가 달라진다.
    1. 여기선 mustache가 뷰 템플릿 엔진으로서 기능을 제공해준다.
        1. template폴더에 .mustache를 생성하는데, 인식을 못하니 플러그인부 설치치
    2. **view template에는 2가지 동료가 있으며, 화면을 볼려면 2가지 동료를 만들어줘야한다.**
        1. 처리:controller
        2. data:model

3. controller는 client로부터 요청을 받는 역할이다. view는 최종페이지를 만들고, 거기에 쓰일데이터들을 model이 만들어준다.
    1. **해석 및 구현 순서: client -> controller 메서드 및 @애너테이션으로 접속요청 + model까지 받아서 -> view 템플릿 페이지에 + 변수값으로 전송

4. 뷰템플릿과 레이아웃 : 화면에 요소 배치
    1. 가장 기본 레이아웃:  header(네비), footer(사이트정보)의 샌드위치 레이아웃이 기본
    2. getBoostrap -> 스타터템플릿 복붙해서 덮어쓰기
        1. navbar 복붙해서 header자리에 넣어주기
        2. div + hr+ p태그로 footer자리에 넣어주기
    3. 1개 레이아웃이 완성되면, 다음 페이지는 템플릿화해서 반영해주기
        1. 섹션별로 코드뭉치들을 layouts폴더>.mustache 파일화 한 다음 -> {{>layouts/header}} 형태로 부른다.
            1. templates 폴더가 기본 >꺽쇠이용 파일템플릿 사용기본경로다.
        2. header 등 윗부분을 짜를 땐 첨부터 자르자.
5. 폼 데이터 주고 받기
    1. form -dto-> controller에서 데이터를 받는 객체가 dto
    2. `폼 페이지`는 layouts폴더처럼, 새로운 articles폴더 > new.mustache로 만든다.
        1. `form` / input태그 / textarea / `button[type="submit"]` 으로 구성한다.
            1. form -> .container with `어디로(action="") + 어떻게(method=""")`
                1. div.mb-3
                    1. label.form-label
                    2. input.form-control `with name에 dto변수명`
                2. div.mb-3
                    1. label.form-label
                    2. textarea.form-control[rows="3"] `with name에 dto변수명`
                3. button.btn.btn-primary[type="submit"]
        2. **post에서 요청데이터를 보낼 때, controller에서는 dto로 받을 것이다. 그 변수명대로 name에 변수명을 지정해서 보내준다.**
            1. **강의에서는 form완성 -> controller완성 -> url+action 지정 -> dto 생성후 controller 파라미터로 지정 -> dto 변수명대로 form에
               name속성주기**
            2. name과 dto변수명만 같으면 **getter없어도 controller속 dto에 알아서 담긴다.**

    3. 페이지를 먼저 만들었지만, 요청 with 데이터 -> -> 변수 넣어서 화면에 뿌려주는 것은 controller가
        1. 페에지 폼 -> 어디서(action, url) + 어떻게(method, post) -> controller 까지 정의해줬다면
    4. **controller가 요청 데이터를 받는 객체 DTO를 만들어줘야한다.**
        1. 컨트롤러와 동일선상에서 dto패키지를 만들고, dto클래스 ArticleForm을 만들어, 데이터를 받아온다.
        2. 필드+생성자+**toString**까지 만든다.
    5. DTO클래스를 정의해준 뒤, controller의 파라미터로 받아준다.
        1. post form -> controller 파라미터 속 dto객체를 지정하면, 거기다가 받아준다는 뜻
        2. 즉, 뷰 페이지의 post form에 맞게 dto을 만들고 -> 받는 컨트롤러의 파라미터로 넣어준다.

6. entity, repository생성후 데이터 저장 실습
    1. form (name) -> dto -> controller 파라미터 -> `database`까지 저장하러 가보자.
    2. 우리는 java를 쓰고, DB는 java를 모르는데, 어떻게 java로 db에게 명령을 할까?
        1. jpa : server(java)의 명령을 -> db가 java를 이해하도록 도와줌.
        2. jpa 핵심도구 2가지 : entity + repository
            1. entity: 자바 객채(dto)를 db가 이해할 수 있도록 규격화해놓은 데이터
                1. my) **dto와 비슷한 필드+@어노테이션을 달아주며 -> 테이블과 1:1매핑되는 클래스**
            2. repository: entity를 db에게 전달하고 처리되게 하는 것
                1. **repository는 dao처럼 interface로 첨부터 만들어준다.?!**
                2. springboot 제공 CrudRepository<entityT,idT> 인터페이스를 extends상속한 인터페이스로 정의한다.
                3. copntroller에서는 인터페이스=추상체 repository를 필드값 변수로 가지되, new 구상체 초기화는 생략하고 @애노테이션으로 자동초기화시켜 springboot제공 기능을
                   가진 구상체로 자동 초기화시킨다. by `@AutoWired`
                4. dto.toEntity한 entit객체 및 CrudRepository상속 레포지토리 (@AutoWeired로 자동구현체초기화)의 .svae( entity ) 결과값으로 나온
                   entity를
                    1. toString()한 것을 각각 찍어보자(보내기전entity, save후 반환되는 entity 출력)
    3. front -> [dto] -> Controller -> [entity] by Repository(일꾼) -> DB 에게 전달 + 처리
        1. dto를 entity로 변환시켜야한다.
           ![image-20220414175216790](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220414175216790.png)
        2. entity를 repository를 통해 db까지 save시킨다.
           ![image-20220414175409690](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220414175409690.png)
    4. H2라는 데베를 이용할 것이다.

7. h2 db에 저장된 데이터 확인하기
    - 리뷰: form -(name to field)-> dto -> controller 파라미터 -(dto.toEntity)-> entity -> repository.save(entity) -> 응답
      entity
        - 찍어보면, id에 null넣은 entity와, repository.save( entity ) 후 응답되는 entity는 null -> db에서 id배정 하여 다르다.
        - dto는 front에서 보내준 xxxForm으로 / entity는 미래 테이블명으로 클래스명을 짓고 있네?..
            - ArticleForm -> front의 Form에서 보낸 데이터를 name-field 일치시켜서 받아주는 DTO 클래스
                - 객체 form -> form에서 날라온 데이터를 받아주는 dto객체
            - Article -> 테이블명 -> entity -> dto에서 변환되는 것

    1. repository.save()한 db데이터 직접확인을 위해 H2 DB 사용설정
        1. src>main>resources> `application.properties`에서 `h2 DB를 웹 콘솔 접근가능 설정`을 적용해줘야한다.
            1. 프로젝트 만들 때 처음 설정해줬던, dependencies > h2 적용한 것을 `web콘솔에서 접속 가능하게하는 설정`
                - `spring.h2.console.enabled=true`
    2. `http://localhost:8080/h2-console`로 h2접속하기
        1. jdbc url은 매번 바뀌는 첨에 접속안될 수 있다. -> 디버그창에서 jdbc검색해서 mem주소 복사해오기 (나중에는 자동설정할 것임.)
        2. /create(insert문) by .save()메서드를 h2콘솔에서 insert 로 넣어보기
            - DB에서 문자열은 홑따옴표, java/python문자열만 쌍따옴표
            - entity에 autoincrement를 지정해줬지만, DB자체에는 autoincrement가 안걸려 있으니, id도 직접 입력해준다.
                - 참고) /create-saveByInsert문 /read-findBySelect문/ update-find+saveByselect+insert문?/
                  delete-deleteBydelete문

8. 롬복으로 리팩토링
    - Lombok: 코드간소화 라이브러리, 게터세터생성자toString -> 필수메서드 코드반복을 최소화 + 로깅으로 println 리팩토링됨.

    1. build.gradle에 롬복라이브러리 추가  ~~롬복 플러그인 설치~~(플러그인은 자동설치되어있음)
        ```
        //롬복 추가
        compileOnly 'org.projectlombok:lombok'
        annotationProcessor 'org.projectlombok:lombok'
        ```
    2. 리팩토링
        1. dto -> entity 속 생성자, toString 리팩토링
            1. @AllArgsConstructor
            2. @ToString
        2. controller 속 println 리팩토링
            1. @Slf4j -> log.info(); // 출력위치도 같이 나온다.

### my 큰틀

1. controller -> GET 메인 뷰 페이즈 -> 레이아웃 ->create(post)요청을 위한 화면 GET /new route + /new form 뷰 페이지 -> create POST용 /create
   route -> 전달dto -> entity, repository.save()
   h2에 저장후 응답entity 찍어서 확인
    - 1~6
2. /create saveByInsert문까지만 하고
    1. application.properties에서 웹콘솔 h2 접근설정 후 접속 `db에서 데이터` 확인
    2. build.gradle에 `롬복` 라이브러리 추가 후 dto,entity 속 `생성자,toString` / controller 속 `println` 리팩토링
    - 7~8
3. 
   
