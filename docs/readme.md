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

6. jpa로 (entity, repository생성 후) 데이터 저장 실습
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

9. jpa로 특정 데이터 조회
    - 복습: /new -> 제출된 form 데이터 ->  (dto)  -> /create -> dto.toEntity() -> (Article) -> repository.save()
    - 데이터 조회의 흐름
        1. 브라우저의 `url로 조회 요청` ex> /articles/1
            - /new에서 post `Form(데이터)으로  생성 요청`이 아닌 `url로 조회 요청`이 다르네!
        2. controller는 `조회요청 url`을 받는다.
            - (데이터가 담긴 form이 아니라 dto로 안받는다?!)
        3. repository에게 `url 속 정보`만 받는다
            - create시에는 dto -> toEntity(Article)을 repository에게 .save( ) 메세지로 전달
            - 조회는 db로 갈때까지 id 등 정보만 가져가고 dto, entity없이 db까지 간다?!
        4. db는 reposotiry가 Entity로 응답해준다.
            - repository는 무조건 entity로 들어갔다가 entity로 나온다. (생성시 id null인 entity, 응답은 db에서 부여된 id를 가진 entity)
        5. post인 create와 달리 entity -> model -> view템플릿으로 전달까지 됨.
    - 자세한 흐름
        - /new(get, form -> post) -> /create -> db에 생성된 후 응답entity를 log찍어 확인했지만, 이제 웹페이지로 확인할 수 있게 하자

        1. post route는 form으로 post를 보내기 위해 `get route + form을 가진 view페이지가 준비물`로 필요했었다
            - 하지만, `조회는 view나 route없이 브라우저url로만 요청`을 보낸다.
            - 크롬을 통해 `localhost/8080/aricles/1`로 `url로 조회요청`을 보낸 뒤, 받아줄 get route를 controller에 작성해보자.
        2. dto(xxxForm)가 없으므로 메서드 파라미터에  `url조회요청 속 가변변수 {id}`를 Long타입으로 받아줄 어노테이션을 적어줘야한다.
            - `@PathVariable Long id`
        3. 실제 url조회요청을 받아서 id를 롬복 log.info()로 찍어보기
        4. **이제 id를 가지고 데이터를 조회하는 3가지 과정이 남았다.**
            1. id로 데이터를 가져오기 : repository.findById( id ) -> repository는 entity로 응답(create시에는entity로 메세지)
            2. 가져온 데이터(entity)를 model에 등록하기 -> 파라미터에서 Model model로 받고, add만 해주면 알아서 뷰템플릿으로 전달된다.
            3. 보여줄 view템플릿 controller return에 미리 명칭 설정해놓고 -> 생성하러 가기: atricles/show.mustache 생성 -> bootstrap table 코드 복붙
                - table은 th+tr/ tbody+tr만 남긴 뒤, model이 자동으로 뿌려주는 [entity 뭉치데이터]를 {{#attribute명}} 담긴데이터의 내부 필드들 쓰기
                  {{/attribute명}}가 가능하다.
                - 모델에 등록된 데이터는 class단위(entity)로 넣고, `# -> / attribute명`을 통해서 불러와 내부 필드들을 사용하자.
                - 쓰고 있는 데이터 h2는 휘발성 db기 때문에 서버 재시작시 사라진다 -> 새로 등록부터 url조회요청한다.
10. `jpa`로 모든 데이터 목록 조회
    - 역시 조회요청이므로 `가변id가 없는 url조회요청`으로 조회한다. -> route url을 잡을 때 /articles/로 잡은 이유가 있다 -> `/도메인s` : 전체 데이터 조회
    - cf) `Repository 인페받아쓰기` ~ `@Entity등 Entity`정의 ~ 등 부분이 jpa로 하는 것이다.
      ![image-20220418024502461](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220418024502461.png)
    - **데이터 = entity = db테이블 마다 -> controller를 작성해서 XXXController에 route를 작성해주도록 하자.**
    - url요청이후 `컨트롤러 READ시 할일 3가지`는 똑같다
        1. DB에서 모든 데이터(articleEntity or articleEntityList)를 가져온다.
            1. jpa제공 CrudRepository(inter)를 extends interface 중에 특정메서드의 응답값 시그니처를 다운캐스팅할 수 있다.
                - CrudRepository(inter) -extends- ArticleRepository(inter)
                - Iterable(inter) - Collection(inter) - List(inter) - ArrayList(class)로 쓴다.
               ```java
               public interface ArticleRepository extends CrudRepository<Article, Long> {
               @Override
               //    Iterable<Article> findAll();
               ArrayList<Article> findAll();
               }
               ```
        2. model에 데이터 등록하기
            - 파라미터에서 Model model 받아 List<Article> 넣기
        3. view 페이지를 설정한다.
            - controller return에 미리 명칭 설정해놓고 -> 생성하러 가기: `return "domains/index"`
            - 전체 데이터 조회는 기본화면 뿌리는 것과 마찬가지기 때문에, `show.mustache`를 완전히 복붙해서 `index.mustache`를 만들면 된다.
            - **list를 넘겼으면 따로 반복문 처리할 필요없이 기존 1개데이터 시작 기호 `{{#article}}`대신 `{{#articleList}}`로만 바꿔주면
              알아서 `반복문코드는 생략된체 반복`되면서 필드만 뽑아쓸 수 있다.**

    - 모든 데이터 조회하는 `/domains`에 접속해도, 현재는 휘발생 db를 쓰고 있기 때문에, 데이터가 없다 -> 껏다 킬때마다 데이어 추가! -> `/domains/new`

11. 링크와 리다이렉트
    - 보통 웹게시판의 구조
        1. index(목록페이지(모든 조회), R)
            - show(상세페이지(개별 조회), R)
                - `뒤로가기` / `수정(submit)` or `삭제(submit)`
            - new(새글입력페이지, C)
                - `뒤로가기` / **`전송(submit)` -> (db -> ) 해당 id의 show(상세페이지)로 이동**
                  ![image-20220420224859056](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220420224859056.png)
    - link는 편리한 request / redirect는 편리한 response가 가능해진다.
        - link: a태그, form태그 등 -> controller에서결과페이지로 응답? or **post와 같이 결과페이지+데이터 응답이 아닌 다른 route로 재요청이 응답되기도 한다.**
            - **new(새글입력페이지, C) -> db에 create -> `post는 결과페이지 응답이 아닌 재요청(redirect)`로 남은 처리를 위임한 곳으로 재요청을 보낸다.**
        - redirect: return 결과페이지 대신, client에게 재요청을 지시하는 것이다.
          ![image-20220420225939105](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220420225939105.png)
            - client가 직접 redirect속 url에 따라서 다시요청을 보낸다.(주로 해당로직에 대한 결과페이지를 보여주는 Read(GET)하는 곳으로 재요청을 보내는 듯)
              ![image-20220420230008120](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220420230008120.png)
            - 그제서야 결과페이지를 응답해준다.
              ![image-20220420230241279](https://raw.githubusercontent.com/is2js/screenshots/main/image-20220420230241279.png)
    - **개념 요약**
        - 현재까지 유일 **`post요청`인 create에 `대해, db처리후 -> 결과페이지를 보여주도록 redirect -> 관련로직에 대한 read or readAll`로 재요청시켜야한다**
        - **`post후 개별조회(/domains/{id}) or 모든조회(/domains)`로 redirect를 생각한다. 그게 `조회에 있는 수정or삭제의 post`든 `생성의 post든`이든**

    - 현재까지 서비스의 문제점
        - `/new`(new.mustache) -> `/create` -> `/{id}`(show.mustache) ->`/`(index.mustache)이 완성되었으면, **index를 시작으로 한 요청
          link 연결고리를 만들어야한다.**
        - `index`(전체데이터 조회)에서 시작하여 -> `show`(개별데이터마다 요청link) or `new` 요청link가 없는 상태다.
            - 각 페이지마다 `뒤로가기` 요청link등 **페이지별로 link를 통한 연결고리가 끊어진 상태이다.**
    - **new/(create)/show/index 완성후 페이지 연결 `요청link` 만들기**
        - `/new`(new.mustache) -> `/create` -> `/{id}`(show.mustache) ->`/`(index.mustache)이 완성되었으면, **index를 시작으로 한 요청
          link 연결고리를 만들어야한다.**
        - 먼저 a태그로 요청link를 만들어주자.

        1. index -> new
            - index.mustache(/domains) 속 a태그 -> /domains/new
            ```html 
            <a href="/articles/new">New Articles</a> 
            ```
            - html은 업데이트후 build만으로 업데이트가 반영된다.
        2. index <- new 뒤로가기
            - **form이 있는 view였다면, form의 submit버튼과 같이 ,form안에 a태그를 추가해주자.**
            - back은 기본적으로 먼저 작성해주고, **index -> new -> 다음 로직을 생각하자**
        3. new -> /create (post) 후 redirect -> show
            - 개별 데이터 생성이니까, 개별데이터 조회인 show(/domains/{id})로 보낸다.
            - 보낼때 id가 필요하므로 응답entity에서 id를 getter해야하는데 **롬복의 `@Getter`를 Entity에 사용한다**
           ```java
           Article saved = articleRepository.save(article);
           final Long id = saved.getId();
           return "redirect:/articles/" + id;
           ```
        4. index(/domains) <- show(메인으로 돌아가기)
            - index에서 바로 온게 아니라, new -> show 로 왔는데 index로 가는 중이라서 Back이 아니라 Go to Main임.
        5. index -> show
            - 전체 데이터가 있는 상황에서 개별로 가기 위한 요청link는 `반복되는 글 제목`에다가 a태그를 달면 된다.
            - **중괄호의 템플릿엔진 문법은 String처럼 쓰면 된다.**
            ```html
            <tr>
            <td>{{id}}</td>
            <!--11-4 반복되는 개별데이터의 글제목에 개별조회 show링크달기-->
            <td><a href="/articles/{{id}}">{{title}}</a></td>
            <td>{{content}}</td>
            </tr>
           ```
            - **/domain/{id} 가변의 개별데이터 조회는,  `개별데이터가 반복되는 전체 데이터 조회 index에서 1개 개별데이터의 요소`에서 a태그로 걸어준.**
                - my) `index(전체데이터조회)`에서 반복문 html + 필드데이터들 뿌려주는 순간, 그 요소중 1개에 a태그가 걸려 -> `show(개별 데이터 조회)`로 이어진다는 것을 생각해보자.  
        6. ㅇㅇㅇ

### my 큰틀

1. controller -> GET 메인 뷰 페이즈 -> 레이아웃 ->create(post)요청을 위한 화면 GET /new route + /new form 뷰 페이지 -> create POST용 /create
   route -> 전달dto -> entity, repository.save()
   h2에 저장후 응답entity 찍어서 확인
    - 1~6
2. (post의 준비물이 GET /new -> form)-> /create -> formDto -> toEntity(null id로 대입) -> save(entity) -> db 저장후 확인용 응답entity
   `log.info()`찍어보기
    1. application.properties에서 웹콘솔 h2 접근설정 후 접속 `db에서 데이터` 확인
    2. build.gradle에 `롬복` 라이브러리 추가 후 dto,entity 속 `생성자,toString` / controller 속 `println` 리팩토링
    3. **create 라우트는 개발하는 동안은 `log.info( dto/nullId Entity/responseId Entity)`를 유지해놓기** -> 휘발성 db에 대해서 매번 create하고 제대로
       됬는지 확인해야한다.

    - 7~8
3. **read는 특정데이터조회 -> 1개 데이터 뿌리는 view(show)개발 ->  전체데이터 조회 -> 전체데이터 뿌리는 view 이자 index화면 -> 반복문 처리 순으로 개발한다.**
    1. 특정 데이터 조회 by url조회요청 속 id :url조회요청with가변id -> /도메인s/{id} -> findById() ->  응다entity -> model에 담기 -> show 뷰템플릿 +
       부트스트랩 table에 찍어보기
        - 메서드이름은 `show` : `/도메인s/{id}` -> show ->  .findById().orElse(null); -> `return "domains/show"`
        - dto -> read시에는 Dto.toEntity()없이 `id만으로 repository로 db 데이터 조회`
        - 응답할 데이터가 db에 없는 경우 -> Optional<Entity> or .orElse(null);
    2. 모든 데이터 목록 조회
        - 메서드이름은 `index` : `/도메인s` -> index ->  .findAll(); **CrudRepository인페-extends->인페에서 시그치너(응답값 다운캐스팅) 오버라이딩 된
          것.** -> `return "domains/index"` -> 개별데이터조회인 `show.mustache`를 복붙한 뒤
            - **머스태치 문법안에 데이터의 묶음 -> 알아서 반복되니 필드만 뽑아쓰도록 그대로 사용하면 된다.**
                - `{{#article}} -> {{#articleList}}`로만 바꾸면 알아서 반복문이 돈다는 뜻이다.

    - 9~10

4. `/new`(new.mustache) -> `/create` -> `/{id}`(show.mustache) ->`/`(index.mustache)이 완성되었으면, **index를 시작으로 한 요청 link
   연결고리를 만들어야한다.**
    1. index -> new (New domain)  + new -> index (Back)
    2. new -> /create post (redirect) -> show + show -> index(Go to Domain List)
        - index에서 왔으면 `back` / 다른데서 index로 갈 거면 `Go to ` / 결국엔 index로 간다.
    3. index -> show (반복되는 개별데이터 속 1개 요소에 a태그)
        - **/domain/{id} 가변의 개별데이터 조회는,  `개별데이터가 반복되는 전체 데이터 조회 index에서 1개 개별데이터의 요소`에서 a태그로 걸어준다.**
    - 11~
   









































