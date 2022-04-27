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

### 설계

![image-20220427154149873](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427154149873.png)

- 화면응답이 아니라, RESTAPI(RestController)로서 -> 데이터(ResponseEntity) 응답하기

![image-20220427154255184](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427154255184.png)

### 17~ RestAPI

1. controller패키지 내부가 아닌 api패키지를 생성 > 테스트용 `FirstApiController.java`
    - controller -> view템플릿 페이지(string) 반환
    - @restController -> json을 반환
        - string을 return하면, 그대로 string을 반환하는 것 같음..
    - talend api로 요청해보기 -> 기본 https설정이므로 s빼고 localhost로 요청하자.
    - 일반 컨트롤러와의 차이
        1. 일반controller: view템플릿 페이지(html)를 반환
        2. restControler: 일반적으로 json반환 but text도 반환가능! -> 데이터를 반환
2. articles를 restAPI로 만들기 위해 `api > ArticleApiController.java` 생성
    - 기존: controller> ArticleController
    - rest: api> ArticleApiController
        - `@RestController`와 함께 컨트롤러안에 4가지 주석으로 4가지 http method 만들 준비하기
        ```java
        @RestController // RestAPI 용 컨트롤러 데이터(주로 JSON) 반환
        public class ArticleApiController {
        //GET
        
        //POST
        
        //PATCH
        
        //DELETE
        }
        ```
3. 개발과정
    1. GET(전체 조회)
        1. talendAPI로 없는 route지만 요청보내서 red status를 받는다.
            - GET `http://localhost:8080/api/articles` -> `404` (조회에러는 NotFound)
        2. mapping method를 컨트롤러에 정의해준다.
            - 전체 데이터조회를 `List<Entity>`로 일단 응답해준다.
            - Entity로만 응답해주면, 알아서 String형태의 Json으로 변환되어 내려가는가보다.
        3. mapping method가 완성되면 다시 talendAPI로 요청해본다.
    2. GET(개별 조회)
        1. api 조회 -> error status -> mapping method작성 -> ok status

        - GET `http://localhost:8080/api/articles/1`
    3. POST(생성)
        1. api 조회 with body `id제외 stringKey json + 중간에만 쉼표`  -> error status(405, 해당 URL 없을 경우) -> mapping method작성 ->
           ok(201) status
            ```json
           {
           "title": "오늘은",
           "content": "치킨을 먹고 싶어라...!"
           }
            ```
        2. **생성시에만, requestDto.`toEntity()`의 entity로 데이터를 생성(save)한다.**
            - 응답은 200이지만, title과 content가 비어있다.
        3. **Controller와는 다르게, RestController에서는 request속 body에 있는 정보를 -> Dto로 받을 때, `@ReqeuestBody`파라미터가 필요하다.**
            - controller: form의 name과 일치만 시키면 파라미터에 dto를 적어주면, 알아서 request Body에서 dto로 꺼내갔다
                ```java 
                  @PostMapping("/articles/update")
                  public String update(ArticleDto form) {
                ``` 
            - restController: request body -> request Dto 로 가져가려면 `@RequestBody`의 애노테이션 필요함.
                ```java 
                  @PostMapping("/articles/update")
                  public Article create(@RequestBody ArticleDto dto) {
                ``` 
    4. PATCH(수정): 생성다음에 만들기
        - PATCH `http://localhost:8080/api/articles/1`

        1. api 조회 -> error status -> mapping method작성 -> ok status
        2. 필요한 request body데이터
            - `조회`-> none or id만 / `생성` -> id빼고 전부 / `수정` -> 수정할 개별id + 수정할칼럼만?(X, sql아님) Entity단위로 db와 소통-> 전체데이터
              json들어와야함! /
            - **생성 -> 수정은 html뿐만 아니라 route도 비슷하다. 생성의 `id제외 requestBody데이터(Entity)` + 수정대상 개별id의`path로 오는 개별데이터의 id`까지**
            - 즉, 수정은 `id를 PathVariable`로 받으면서 + `수정완료된 row 1줄 json full 데이터를 RequestBody`로 둘다 요구된다.
              ![image-20220427184547667](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427184547667.png)
        3. **복잡한 수정용 route 4가지 로직 정리**
            ```java
            @PatchMapping("/api/articles/{id}")
            public Article update(@PathVariable Long id,
                                  @RequestBody ArticleDto dto) {
                                  //1. 수정용 엔터티 생성
                                  //2. 대상 엔터티 조회
                                  //3. 잘못된 요청 처리(update, delete는 DB에 존재하는 개별id 1개를 `개별id조회`후 ->?-> `처리` 대상이므로)
                                  // - 대상이 DB에 없거나 or id가 다른 경우
                                  //4. 업데이트 처리 및 정상 응답(200)
                                  return null;
            }
            ```
            - log.info()에 2가지 이상찍기:  중괄호{}로 남긴 포매터 + 콤마로 연결해주면 된다.
                - `log.info("id: {}, article: {}", id, article.toString());`
        4. **템플릿엔진의 요청처리 vs restAPI요청처리 정리해보기**
            - 템플엔진: 잘못된요청 처리 X 제대로된 요청(데이터존재) -> 처리 + flash msg -> redirect
            - restAPI: 잘못된요청을 early 400+body(null) return -> 이후 제대로 된 요청 처리 -> 200 + entity return
        5. **entity내 `patch` 메서드 -> SQL update set처럼, `수정할 데이터만 보내도 되도록 처리`해주기(`수정안된 칼럼은 알아서 채워진 체로 Entity생성하게 처리`)**
            - default로, `id` + `수정할 칼럼데이터만 json`으로 보내면, toEntity()시 생성자에 null로 채운 Entity가 만들져서
              -> `수정안할 칼럼을 안주면 null로 바뀌는 상태`가 된다.
            - **db에서 꺼낸 `targetEntity`.patch( <- `일부데이터만 가진 Entity` )로 내부 데이터가 존재하면 붙여주는 메서드를 entity내부에 만든다.**
            ```java
               
            public void patch(final Article article) {
            //17-9. 수정안되서 requestbody에 안들어간 칼럼은 null로 차있다. -> patch로 붙여준다는 것은
            // -> 각 칼럼들에 대해 != null로 존재하는 칼럼만 붙여준다(갱신)는 뜻이다.
            // -> 같은 entity객체내 비교라서, 필드를 편하게 꺼내서 확인하면된다.
            if (article.title != null) {
            // 데이터가 존재한다면, target Entity에 넣어줘 없음 말고...
            this.title = article.title;
            }
            if (article.content != null) {
            this.content = article.content;
            }
            }
            ```
            - 모든 데이터를 다 보내지 않고, 수정하고 싶은 칼럼만 json으로 넘겨도 가능해진다.
                - 필수인 id를 제외하고, content만 수정 -> title을 생략한 데이터를 json으로 던져도 데이터가 수정된다. like SQL update
                ```json
                // http://localhost:8080/api/articles/1
                {
                "id":1,
                "content": "치킨을 먹고 싶어라123123"
                }
                ```
                - update시킨 뒤, 확인은 조회/개별조회에서 안하고, Response탭의body에서 해도 된다.
    5. delete
        - DELETE `http://localhost:8080/api/articles/1` -> 이 문구를 가져가서 mapping method를 만드는 것

        1. api 조회 -> error status -> mapping method작성 -> ok status

### 큰틀

1. `api`패키지 > `DomainApiController.java` 만들고 -> `주석으로 4가지 메서드 나누고` -> 요청페이지 대신 `Talend API` 등 API요청 기기를 펼처놓고 -> 순서대로 빨간줄
   뜨게 요청후 url복사해서 -> controller에서 메서드 작성
2. 템플릿엔진이 순서와 조금 다르게 `GET(조회) 2개`부터 처리하여 -> POST -> PATCH -> DELETE 순으로 완성하자
   ![image-20220427215505571](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220427215505571.png)
3. **조회후 처리하는 PATCH/DELETE**는 `조회 -> 잘못된요청 -> 처리`에서 **조회후 if잘못된 요청일 때, `BAD_REQUEST`인 early 400 return해줘야한다.**
    - update(2): DB에 없는 데이터(target == null) || PathVariable의 요청URL속 id <-> 요청body에있는 json속 id가 다른 경우
    - delete: DB에 없는 데이터(target == null)
        - 400: return `ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);` / update, delete 잘못된 요청시
        - 200: return `ResponseEntity.status(HttpStatus.OK).body(updated);` / update성공시 .body()에 데이터 싣어서 OK
        - 200: return `ResponseEntity.status(HttpStatus.OK).build();` / delete 성공시 데이터 없이 OK
4. update시 수정될 칼럼데이터만 json으로 보내도 그 부분만 바뀌도록 target entity(db에서 id로 꺼낸)에 데이터 있는것 만 `patch`해주도록 entity내 patch메서드 만들기
