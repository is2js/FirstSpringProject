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

### (CRUD이후) 15~

15. CRUD 동작으로 인한 SQL쿼리문 보기를 위한
    1. jpa 로깅 설정
        1. application.properties에서 SQL을  `디버그` 로그레벨로 설정 + 쿼리 이쁘게 보여주도록 설정(format형태로 출력) + 물음표 바인드변수 출력
            - `logging.level.org.hibernate.SQL = DEBUG`
            - `spring.jpa.properties.hibernate.format_sql=true`
            - `logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE`
        2. jdbc 내장 mem db 고정 url설정
            - `spring.datasource.generate-unique-name=false`
            - `spring.datasource.url=jdbc:h2:mem:testdb`
    2. **entity에서 1부터 시작 vs `data.sql`로 3개 미리 생성 -> 생성시 1부터 시작되어 에러**
        1. Entity의 어노테이션 `@GeneratedValue` : DB데이터 고려하지 않고 무조건 1부터 시작
        2. DB에 이미 생성된 데이터(초기데이터 생성용 data.sql포함)를 고려해서 DB가 번호 +1씩 번호부여하도록
           전략변경 `@GeneratedValue(strategy = GenerationType.IDENTITY)`
    3. 생성 -> 전체조회/개별조회 -> 수정 -> 삭제 순으로 sql이 어떻게 찍히나 살펴보기
    4. @Entity에 의해 생성되는 create table문을 복사해서 h2-console에서 쿼리 연습하기

16. RestAPI 와 JSON(Placeholder)
    1. 개념
        1. 다양한 클라이턴트 존재: 브라우저(html문서 응답) 뿐만 아니라 모바일(스마트폰용 화면), 워치(워치용 화면)
        2. 모든 client 기기에 대응하려면? **RestAPI: 웹서버의 자원을 클라이언트에 구애받지 않고 사용할 수 있는 자원**
            - 모든 기기에 통용되려면, 화면이 아닌 데이터(json형태)로 응답해야한다. (과거 XML -> 최근 JSON으로 통일되는 형태)
                - XML: 사용자 정의용 html
                - JSON:자바스크립트 방식을 채용한 객체표현식
    2. 실습: 연습용 REST API서버를 활용해서 http 요청/응답을 연습
        - 연습용 fake rest api server 사이트: `https://jsonplaceholder.typicode.com/`
        - 크롬 확장 프로그램: `TALEND API tester` 설치

        1. GET
            1. `200`(정상 조회): https://jsonplaceholder.typicode.com/posts/1
            2. `404`(NotFound, 자원X): https://jsonplaceholder.typicode.com/posts/101
            3. HTTP요청/응답 문자열 확인 - 아래 HTTP탭
               ![image-20220427145935878](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427145935878.png)
        2. POST for create
            1. html에서는 form으로 보냈지만
            2. js fetch에서는 **JSON.stringify(`{ }`)**로 만들어서 보낸다.
                - form대신 json의 형태로 TalendAPI BODY안에 `{}`에 입력하면 된다.
            3. `201`(잘 생성): https://jsonplaceholder.typicode.com/posts/
                ```json
               {
               "title": "오늘은 왠지...",
               "body": "치킨을 먹고 싶어라...!"
               }
                ```
            4. 문자열 확인
               ![image-20220427150857399](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427150857399.png)
            5. POST요청의 body안에 key부분에 "쌍따옴표"를 빠뜨린 경우 에러는?
                - `500`(서버 내부 문제)
                    - BODY쪽을 보면, at JSON.parse가 가장 먼저 뜬다 -> 보내는 JSON형식이 잘못되었을 것
                    - HTTP문자열 쪽을 보면, 응답의 body에 에러내용이 같이 온다
                        ```
                        SyntaxError: Unexpected token t in JSON at position 4
                        at JSON.parse (<anonymous>)
                        ```
                ```json
               {
               title: "오늘은 왠지...",
               body: "치킨을 먹고 싶어라...!"
               }
                ```
        3. PATCH for update/edit
            1. patch + 수정할 개별id URL + body에는 수정할 데이터만(수정은 모든칼럼 안넣어도됨) 넣어서 보낸다.
            2. `200`(잘 수정도 200, 잘 생성만 201): response body에 수정된 데이터가 같이 내려온다.
               ![image-20220427151725154](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427151725154.png)
        4. DELETE
            1. `200`(성공은 잘생성만 201, 나머지 200) 에러는 조회만 404, 나머지는 500

### 큰 틀

15. entity -> jpa에서 db생성 query를 보려면
    1. applicatino.properties에 몇가시 설정을 해줘야한다.
        - `logging.level.org.hibernate.SQL = DEBUG`
        - `spring.jpa.properties.hibernate.format_sql=true`
        - `logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE`
    2. jdbc 고정 URL 설정
        - `spring.datasource.generate-unique-name=false`
        - `spring.datasource.url=jdbc:h2:mem:testdb`
    3. entity에서 초기데이터 생성(data.sql)를 고려해서 id를 부여하도록 `@GeneratedValue(strategy = GenerationType.IDENTITY)`으로 변경

16. RestAPI개념과 json placeHolder + talend api
    1. 모든 client 기기에 대응 하는 자원은 화면이 아닌 json형태로 데이터를 응답하는 `restAPI`
    2. 실습 준비물
        - 연습용 fake rest api server 사이트: `https://jsonplaceholder.typicode.com/`
        - 크롬 확장 프로그램: `TALEND API tester` 설치
    3. C(201)R(404)UD(200, 500)
        - 크게 5가지 분류이나, 현재 나온 것들먄 요약해보자.
          ![image-20220427153623835](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427153623835.png)
          ![image-20220427153615298](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427153615298.png)
    4. HTTP요청/응답 구조 -> 문자열로 작성된다.
        2. 첫째줄 요청 내용 / 첫째줄 응답 상태정보
        3. 그다음 요청/응답정보인 header (편지봉투)
        4. 요청 body /응답 body (편지내용물)
           ![image-20220427152850342](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427152850342.png)
    5. body에 들어갈 json은 js -> `JSON.stringify()`로 만들어줘야지 넘어가고, 다시 넘어올 때 `.json()`를 해줘서 **통신과정은 string으로 한다**
        - TalendAPI에서 입력해주는 BODY에는 key값도 string으로 넣어줘야함을 잊지말자(api전용 통신은 string형태로 통신됨)
        - js(실제json)나 controller에서만... key(stringX)-value의 map형태
          ![image-20220427153853206](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427153853206.png)
