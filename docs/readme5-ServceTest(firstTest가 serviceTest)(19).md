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

### 서비스 테스트

- 테스트를 위한 비용(매번 브라우저 -> 요청 -> `서비스` -> 응답 -> db도 확인)을 최소화할 수 있다.
- 과정
    1. 예상(2) 실제값과 비교가능하도록 (실제를 예상한)예상값 만들기
    2. 실제(1)
    3. 비교
    4. 통과시 리팩토링 / 실패시 디버깅
- 테스트 케이스: **`매 메서드마다` 성공케이스 + 실패케이스를 모두 작성한다.** -> 미리 `method_성공`,`method_실패`를 작성해놓자.
    1. 성공 케이스 `메서드_성공____성공하는_케이스_중_1가지_경우_설명`
    2. 실패 케이스 `메서드_실패____실패하는_케이스_중_1가지_경우_설명`
       ![image-20220428152151321](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220428152151321.png)

- 테스트 방법
    1. 서비스의 메서드명로 가서 우클릭 -> generate -> test -> 메서드 클릭
    2. 테스트클래스에 `@SpringBootTest` 달아주기
    3. `@Autowired`로 이미 객체가 생성된 service DI해주기
- 참고
    - list vs list를 `assertEquals( toString(), toString())`으로 비교하면 된다?!
        - **각 객체를 comparable로 안 만들어줘도 되니 `list 순서가 같아야한다`.**
        - 객체도 .toString()으로 비교해버린다.
    - 대문자 `Expected` = expected와 동일한 값 = `만들어준 정답 값` vs `Actual` = 실제로 들어간 값
- 테스트 코드 작성요령
    1. **`실제`의 메소드에 인자가 필요하면, 그 필요한 데이터(인자)를 `변수`로 둔다.**
    2. `예상`에서 그 `실제에 필요한 변수(인자)`를 사용자가 입력했다고 가정하고 `해당케이스에 맞게` 만들어준다.
    3. **`그 인자`에 따라 실제값을 -> 예상해서 -> 예상값을 만들어준다.**
        - `db에 생성의 경우, 예상값은 다음 id를 달고 나오는 것으로 예상`해서 응답entity를 만들어서 비교한다.
    4. 만약, 특정객체자리에 null이 반환되더라도, 객체 객체변수 = null;형식으로 입력해서 비교해준다.
        - null은 toString()이 안되므로 비교시 주의한다.
        - `NPE` : null이 넘어와서 메서드 호출됬다!고 생각하자.
        - **null 가능성이 있는 객체 비교시 toString()등 메서드의 호출없이 객체 vs 객체를 비교하자.**
        - null과 null은 assertEquals()가 가능하다.
    5. **하나하나 성공한 테케가, 전체 돌렸을 때 에러가 발생한 경우?!**
        - 대표적인 예: index(조회) -> create(생성) 순으로 하나씩은 성공했는데 **한꺼번에 실행 -> 메서드명순으로 실행 -> create부터 실행 -> 최초 데이터수(예상값으로 적었던)보다 실제 조회(index)시 데이터가 더 많아지는 경우**

        1. `Expected`에서 내가 실제값을 예상한 값을 확인한다.
        2. 전체테스트를 돌리다보니 발생한, 실제값 `Actual`을 확인한다.
    6. 조회를 제외한 db관련 service들 -> **조회보다 먼저 실행되면 에러 발생** 
         - **db변경 service method 테스트시 @Test에 추가로 `@Tranactional`달아주기
         
### 댓글 모음


오늘 날짜로 실습을 진행중인데
21:29 부분의 테스트 코드를 위한 트랜잭션 부분에서 실습 실행 결과, 별다른 오류를 발생시키지 못했습니다.
혹시 최근 스프링부트에서 자동으로 테스트 코드 실행 시 트랙잭션을 지원한다던가, 아니면 임의의 경우에 오류를 발생하지 않을 수 있을까요?

홍팍
1개월 전
코드를 살펴봐야 알 수 있겠지만,
예상하기로 테스트의 실행 순서가 달라
오류 없이 통과한게 아닌가 합니다.

index 테스트가 먼저 수행된 후,
create 테스트가 수행되었다면
테스트가 통과될 수 있어요.



Encho엔쵸
​ @홍팍  그렇다면 테스트의 실행 순서는 탑다운 방식이 아닌 임의로 지정이 가능한건가요?



홍팍
@Encho엔쵸  JUnit5는 메소드명을 기준으로 실행 순서를 정합니다.

필요에 따라
테스트 실행 순서를 명시할 수도 있는데요
@TestMethodOrder와
@Order 어노테이션을 사용해보세요

더 자세한 내용은 공식 문서
https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-execution-order

또는 구글링을 통해 확인해보세요.
"JUnit5 테스트 실행 순서"

좋은 질문 감사합니다👍



Encho엔쵸
@홍팍  덕분에 쉽게 배워갑니다. 감사합니다. 👌👌


홍팍 @Encho엔쵸  많은 공유 부탁드려요 굽신굽신
