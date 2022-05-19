### 상세
- 이전에 `entity` -> `repository` -> repository test까지 했었다.
	- 이번에는 controller와 service를 추가해서 `댓글 CRUD를 완성`한다.

1. service이전에 `controller부터` api package에 만든다.
	- 이후 service는 controller에 `@Autowired`주입될 놈으로 빨간색으로 만든다.
	![20220518172742](https://raw.githubusercontent.com/is2js/screenshots/main/20220518172742.png)
	- service도 내부에서 `@Autowired`주입으로 repository를 갖다쓴다.
2. service에서 하위entity(다, comment)에서는 상위entity(일, article)의 repository도 주입받는다.
	- 하위entity가 `자동조인을 통해` 상위entity를 가지고 있기 때문에 - 편하게 얻어낸 상위entity를 데이터로 바꿔쓰려면 상위entity repository도 필요하다.

3. controller - sevice - repository들 주입 완료후 **service가 아니라 다시 `controller로 돌아와 main 로직을 작성`준비한다.**
	- restcontroller는 초기데이터가 있다고 가정하고, 아래와 같은 순서로 개발한다
		![20220518180203](https://raw.githubusercontent.com/is2js/screenshots/main/20220518180203.png)
	- **controller도 talendAPI로 uri를 세팅해놓고 붙여넣기로 개발하자**
		![20220518180123](https://raw.githubusercontent.com/is2js/screenshots/main/20220518180123.png)

### restcontroller 작성
1. 전체 조회
	- uri에 `상위도메인/{id}/(현재의)하위도메인s`로 전체조회한다.
	- **일대다/다대일의 `다`를 차지하는 하위도메인은 항상 상위도메인의 id를 물고와야한다.**
		- 하위도메인은 항상 상위도메인의 id를 `<hierarchy용> PathVariable`로 물고 있는다 cf) RequestParam = QueryString
		- **uri상의 {id} pathvariable과 메서드인자에서 쓰는 Long articleId가 서로 다른 경우, @PathVariable(value = "id")처럼 [uri상의 변수값]을 value=""에 넣어주면, 원하는 파라미터 이름 articelId으로 사용가능해진다.**
	- 응답에서는 list dto를 responseentity에 싸서 보내고
		- 조회에서는 따로 requestDto를 받는 것이 없으므로 상위도메인Id만 받아 하위도메인 묶음을 조회하도록 한다.
	- 내부에서는
		1. `로직을 service에게 위임`하여 들어온 데이터 -> list 를 받아오도록 역할 위임하여 메세지를 보낸다.
		2. `응답값을 return`한다
			- 이 때, **controller는 무조건 good응답을 응답하도록 한다.**
			- service에서 예외처리를 할 예정인가보다.
	- service위임역할을 짜기전에 commentDto부터 생성해서 만들어준다.
		- dto는 롬복으로 4가지를 기본 세팅해준다.
			1. AllArgs생성자
			2. NoArgs생성자
			3. Getter
			4. ToString
		- dto에서는 상위도메인 entity가 아닌 fk(Id)를 Long으로 가지게 하면 된다.
	- controller구성 + 응답dto까지 완성되면 -> service.위임메서드()를 만들러가면 된다.
	- service내에서 repo가 가져온 entity를 dto로 변환하는 방식은??
		- dto.from()등을 써도 될 것 같은데 (싼것.의존(비싼것))
		- `싼것클래스.create()`의 정팩매로 바로 받아와서 내부에서 필드하나하나쪼개는 `정팩매`를 사용해본다.
			- 싼것.toEntity()는 가능하나 비싼것.toDto()는 안되므로 **비싼것.`toDto()`를 배제한다면, `싼것.정펙매(비싼것)`으로 가져가자**
			- **my) 싼것으로 변환할 땐, 비싼것. 시작이 아니라 `[비싼것을 (인자)로 넣어서 비싼것을 의존하는 싼Class.정팩매(비싼것)]`로 만들자.**
				- **`정팩매`는 싼것.정팩매( 비싼것의존 )의 `객체변환용`이다!!**

	- controller -> service까지 완성되면 -> 서버를 돌려서 최초 talendAPI로 호출하여 확인한다.

		
2. 필요하다면 개별조회
	- **하위도메인은 상위도메인id -> 묶음으로 조회되어서 개별조회가 없다고 보면 된다.**

3. 생성
	- talendAPI에서 `전체조회와 동일한 uri`를 갖는다(GET->POST). 대신 생성에는 `생성시 필요한 데이터 without Id`도 같이 간다.
		- 상위도메인의 id에 묶인 상태의 uri로 개별 1개를 생성하게 된다.
		- **uri에 상위도메인id가 PathVariable로 controller로 가지만, requestBody에도 하위도메인id(PK)를 제외한 모든 것인 `상위도메인(fk)를 controller입장에서 중복이라도 body에 포함`시켜서 건네야한다**
			![20220518214918](https://raw.githubusercontent.com/is2js/screenshots/main/20220518214918.png)
	- 이후에는 [큰틀](#큰틀) 속 `하위도메인 생성부분` 참고

	

### 큰틀

1. structure 개발 순서
	- (이전) `entity` replace DB DDL -> `repository`완성상태
	- `controller생성` -> `service주입받으며 생성` -> 만들어둔 `respository 주입` -> 자동join으로 쉽게 얻어낸 상위entity를 쓰기 위해 `상위 entity repository도 주입`
		- **하위도메인 생성시, 상위도메인 객체를 가지고 있기 때문에**
			- **상위도메인id in PathVariable -> 상위entity 객체 조회 -> 있어야 하위entity를 생성 by`new ( , 상위entity 객체, )`이 가능해진다.**

2. RestApi `controller` 개발 순서
	- `초기 더미데이터 with entity(db) + data.sql`가 있다고 가정할 때, POST(Create)보다 `조회부터 먼저` 만든다.
	- **`RestAPI의 controller` cf) readme3 참고**
		0. 요청uri with talendAPI -> api폴더> controller로 가져가서
		1. 전체조회 with 초기 더미데이터
		2. 개별조회
		3. 생성... 순으로 개발한다.
	- 일반 `템플릿엔진의 controller` `readme` 참고
		1. `main화면` 작성 이후
		2. 생성 with 생성화면
		3. 개별조회 with 개별조회 화면
		4. 전체조회 with 개별조회에 반복문 ... 순으로 개발했다.

3. RestAPI 하위도메인 controller 내부 개발 순서
	1. 상위도메인 묶음의 `전체조회`
		1. entity/repository 생성된 상태 -> controller 명시 -> service주입하며 생성 -> service 명시 및 하위 일꾼들(repo) 주입
			- 하위도메인의 service면, 상위도메인의 repository로 바로꺼낸entity를 활용하기 위해 필요하다.
		2. talendAPI에서 uri 확정 -> 복사해서 가져가서
		3. controller 구조 잡으면서
			1. 응답dto 생성 ( + 생성/수정시 요청dto 생성)
			2. service위임하면서 생성
			3. 응답값 반환(good 응답만! bad는 service내부에서 터짐)
		4. service 메서드 생성하면서 내부로직 완성
			1. 상위도메인id로 묶음을 전체 조회
			2. entity list to dto list
				- entity(비싼것) to Dto(싼것)에 대해서는 `비싼것.toDto()`는 없다. `싼것Class.싼것으로변환_정팩매( 비싼것 )`으로 `싼 객체로 변환`한다.

	2. 상위도메인에 의존하는 `생성`
		1. talendAPI에서 uri + body 확정 -> 복붙해서 가져가기
			- pathVariable에 상위도메인id 있어도 생성요청body에도 fk가 들어간다(pk만 없음)
		2. controller 구조 잡으면서
			1. 응답dto 생성 + 요청dto 생성(구조 동일하다면 공용)
			2. service위임하면서 생성
			3. 응답값 반환(good 응답만! bad는 service내부에서 터짐)
		4. service 메서드 생성하면서 내부로직 완성
			1. 하위도메인 생성시, 상위도메인 객체를 필드로 가짐으로 인한 `상위도메인 먼저 조회`
				- 상위도메인 조회후 .orElseThrow()로 `[생성예외1]` 발생
			2. 상위entity를 필드에 넣을 인자로 받는 `하위dto.toEntity()`로 하위entity만들기
				- 저장되기 전 `빈id Entity`를 만들어야 repo로 .save()가능
			3. **`예외발생 지역인 service내`에서 호출되는 [빈id requestDto -> 빈id Entity]를 만드는 `생성요청Dto.toEntity( )`에서 `[생성 requestDto의 검증]`을 해준다.**
				- service 메서드내에서 인자로오는 arcticledId 및 requestDto에서 get으로 꺼내서 검사해도 될 것 같지만, `그것을 사용하는 dto의 메서드(requestDto.toEntity()) 내부`에서 하면 감추고 더 좋을 듯?!!
					- cf) not null 검증 / null검증이 이어질텐데, 댓글을 참고하여 [hudi builder패턴](https://hudi.blog/effective-java-builder-pattern/)을 참고하자.
						- my) builder: `매개변수 4개이상` + `null검증을 각 builder에서 하며 안전하게 객체 생성`
				- dto내부 toEntity( 상위entity )메서드 내부에서 검증하는 `requestDto - 상위도메인id`의 검증 후 예외발생
					1.  빈id가 아니면 `예외발생2`
					2.  path속 상위id(조회후 상위entity 속 id)와 Dto속 fk(상위id)가 다르면 `예외발생3`

			4. repo로 저장후 `id채운 entity`를 반환받은 뒤 entity to dto by `싼것Class.정팩매_(비싼것)`
			5. 생성후 변환된 dto 응답
		5. 막간의 debuging과 @JsonProperty
			1. 예외발생 시키는 조건문에 breakpoint찍기
			2. 앱 다시 실행시킨 뒤, talendAPI로 요청까지 보내기
			3. 해당메서드, 해당Class에 걸린 것 확인하고 -> 변수들 값 비교하기
				![20220519010553](https://raw.githubusercontent.com/is2js/screenshots/main/20220519010553.png)
				- **`dto에 값이 null`이면 json에서 값을 못 받아온 것 -> 다른 필드는 차있다? dto 필드 선언이 잘못된 것**
					- 비교대상인 entity에는 정보가 잘 있음.
			4. **`FK 변수명`의 db 및 요청json(snake_case)과 java(CamelCase)의 괴리**
				- **필드명에 `@JsonPropery(value ="db 및 json상의 snake_case_id")`로 명시만 해주면 잘 받아와준다.**
					![20220519011003](https://raw.githubusercontent.com/is2js/screenshots/main/20220519011003.png)
		6. talendAPI로 요청보내서 생성확인
		 - 생성요청은 ok라도, 생성된 것의 확인은? response body를 통해 내려보내준 dto를 역시 talendAPI의 response BODY를 확인하면 된다.
		 	- `id(PK)가 배정`되어서 생성되었는지 확인하자.
		 	![20220519011244](https://raw.githubusercontent.com/is2js/screenshots/main/20220519011244.png)
		7. **talendAPI로 잘못된 요청보내서 예외확인**
			- 예외를 발생하도 catch를 안해준 상태이므로, 앱이 멈추게 된다.
				1. 조회되지 않는 상위도메인fk를 보내기
					![20220519011811](https://raw.githubusercontent.com/is2js/screenshots/main/20220519011811.png)
					![20220519011755](https://raw.githubusercontent.com/is2js/screenshots/main/20220519011755.png)
				2. pk id를 가진 dto 보내기	
					![20220519011641](https://raw.githubusercontent.com/is2js/screenshots/main/20220519011641.png)
					![20220519011651](https://raw.githubusercontent.com/is2js/screenshots/main/20220519011651.png)
				3. path vs 요청dto body속 상위도메인의 fk가 서로 다르게 보내기
					![20220519011704](https://raw.githubusercontent.com/is2js/screenshots/main/20220519011704.png)
					![20220519011716](https://raw.githubusercontent.com/is2js/screenshots/main/20220519011716.png)

	3. **하위개별id가 포함되는** `하위도메인 자체 수정`은 Path에 상위도메인id가 없다.
		![20220519085044](https://raw.githubusercontent.com/is2js/screenshots/main/20220519085044.png)
			- 조회 in 상위도메인id 묶음 조회
			- 생성 in 상위도메인id -> 상위entity 필요
			- **수정은 상위도메인id없이 하위도메인id만 필요하다(묶어서 삭제/수정시 상위entity 필요하지 않음)**
		1. talendAPI에서 `상위도메인없이 `api/domains/:id`
			- id만 맞춰주고, 그외 변하거나 변하지않은 필드들을 다 넣어준다. PATCH를 사용한다.
			- body에 포함된 상위도메인id(fk)를 통해, 내용자체는 상위에 속하도록 작성한다.
				![20220519085728](https://raw.githubusercontent.com/is2js/screenshots/main/20220519085728.png)
		2. uri을 controller에 복붙하고 가되, **비슷한 구조의 생성controller를 복붙해서 만든다.**
		3. service 메서드 생성하면서 내부로직 완성
			- 조회를 제외하고 다 Transactional을 붙인다.
			1. 댓글 조회(존재 검증) 및 예외 발생
				- `수정예외1` : 자신id 존재 검증(조회후 없으면 예외)
				- my) `수정예외추가`: 수정요청body에 있는, id외 id대용 uniquefield, 수정으로 생성되는 것의 중복검증
			2. 생성용 dto.toEntity(상위entity)가 아니라 **`수정용 entity.patch(dto) - setter`를 호출하며 service내 사용 메서드 내부로서 `uri + dto 검증`까지**한다.
				- **setter는 싼것/비싼것 의존을 떠나서, 같은객체 연산아닌 이상 setter형태는 무조건 발생하고 복잡해지니, 싼것/비싼것의존을 잊고, `setter당하는.patch( setter할 데이터객체 )`형태로 만들어주면 된다.**
					- setter는 비싼것 상관없이 [update될객체].setter([데이터보유객체])형태로 넣어준다.
				- 생성과 마찬가지로 **dto + entity로 -> entity 변환하여 dto를 사용할 때, dto를 검증한다.**
				- `수정예외2`: uri속 id vs 수정요청body(json->dto)속 id 같은지 확인
		4. 서버 재시작후 talendAPI로 수정잘되는지 확인
		5. talendAPI 예외 잘 발생하는지 확인
			1. 자신id( in PathVariable) 조회 안되면 예외 -> 조회안되게 없는id 던지기
				![20220519094633](https://raw.githubusercontent.com/is2js/screenshots/main/20220519094633.png)
			2. pathVarible 속 자신id vs requestDto 속 id 다르면 예외
				![20220519094828](https://raw.githubusercontent.com/is2js/screenshots/main/20220519094828.png)
	4. 수정과 동일한 uri를 가지는 `하위도메인 자체 삭제`
		1. 수정과 동일해서 controller 복사해서 사용
			- json이 들어오지 않으므로 dto삭제
		2. service 메서드 생성하면서 내부로직 완성
			1. 댓글 조회(존재 검증) 및 예외 발생
			2. json -> dto가 없어서 그외 검증 예외는 발생X
			3. 응답시 조회로 받은 target entity를 -> dto 변환후 반환
		3. 서버재시작 후 talendAPI로 정상확인 예외 발생 확인
			1. 삭제할 자신id 조회 안되면 예외
				![20220519151000](https://raw.githubusercontent.com/is2js/screenshots/main/20220519151000.png)






### 하위 도메인 restAPI 예외 정리
1. 생성 예외
	1. 상위도메인id(fk in PathVariable)이 조회 안되면 예외
		- `도메인 생성 실패! 대상이 없습니다!`
	2. 빈id requestDto가 id(PK)가 존재하면 예외 (in .toEntity() in service)
		- `도메인 생성 실패! 도메인의 id가 없어야 합니다.`
	3. pathVarible 속 상위도메인id in 조회후 상위entity vs json -> requestDto 속 상위도메인id(fk) 다르면 예외
		- `도메인 생성 실패! 상위도메인의 id가 잘못되었습니다.`
	4. my) 댓글자체 중복검증?!
2. 수정 예외(path에 상위id없음)
	1. 자신id(in PathVariable) 조회 안되면 예외
		- `댓글 수정 실패! 대상이 없습니다.`
	2. my) id외 unique field 수정요청시, 조회하여 존재하면 예외(id처럼 uniquefield 중복검증)
	3. pathVarible 속 자신id vs json->requestDto 속 id 다르면 예외
		- `도메인 수정 실패! 잘못된 id가 입력되었습니다.`
3. 삭제 예외(path에 상위id없음)
	1. 자신id(in PathVariable) 조회 안되면 예외
		- `도메인 삭제 실패! 대상이 없습니다.`





### 댓글


CommentDto를 Comment로 변환하는 메소드(createComment)를 Comment 클래스에 만드셨는데

Dto 클래스에서 만들어도 상관은 없을까요 ?
```java
//Dto 클래스
class CommentDto{
   /*
     *  해당 Dto 클래스 객체에는 JSON으로 요청받은 데이터가 들어있음
     */

   public Comment toEntity(Article article){
         // 예외 처리 코드 추가

        return Comment.builder()
                .nickname(this.nickname)
                .body(this.body)
                .article(article)
                .build();
    }
}

// 호출
Comment comment = requestDto.toEntity(article);
```java
이런 식으로 CommentDto에 toEntity()라는 메소드를 만들어서 Comment 클래스 타입으로 변환하도록 해도 상관은 없는거죠 ?

홍팍
3개월 전(수정됨)
네, 두 방법 모두 가능합니다.

- 개인적으로는 클래스 메소드 호출로 변환하는걸 선호합니다.
```java
// 클래스 메소드를 통한 엔티티 생성
Comment comment = Comment.createEntity(commentDto);
```

3개월 전
 @홍팍  그렇군요

그럼 그냥 개인이 선호하는 방식이나 같이 협업하는 팀의 규칙을 따르면 되는거겠네요 ㅎㅎ

혹시나 제가 모르는 다른 이유가 있을까봐 질문 드렸는데 역시나 빠른 답변 감사합니다


게시글에 대한 restapi구현에서는 엔티티를 반환하던데 댓글에 대한 restapi구현에서는 dto를 반환하는 이유가 있나요?


홍팍

제공되는 데이터와실제 데이터를 구분하기 위함입니다.
DTO가 클라이언트에게 제공되는 요리라면,
엔티티는 DB에 담겨진 날것의 식재료인데요

DTO와 엔티티를 구분하면
더욱 유연한 데이터 관리가 가능합니다.

더 자세한 내용은
"엔티티 DTO 차이" 또는
"엔티티 DTO 구분" 혹은
"엔티티 DTO 나누는 이유" 정도로
구글링해보세요👍

PS.
클라이언트에게 제공하는 데이터는
DTO로 만드는 것이 좋습니다.

그렇다면 게시글에 대한 rest api구현에서도(ex 조회기능) 엔티티를 반환하기보다 dto를 반환하는게 더 이상적인거죠? 결국 둘다 json 데이터를 반환하지만요.


홍팍
2개월 전
 @robot  네 맞습니다👍