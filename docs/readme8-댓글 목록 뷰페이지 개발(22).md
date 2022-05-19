### 기본
- 뷰가 달린 것은 ApiController가 아니라 Controller에서 받아준다.
	- uri에 api가 안달린 체로 개발되었음.
		- 전체조회이자 메인페이지인 `localhost:8080/articles`
		- 개별조회 페이지 `localhost:8080/articles/{id}`
- **RestController와 동일한 repository 및 entity를 공유하고 있으므로 데이터는 동일하다.**
	- 다만, view의 html은 rest요청(by talendAPI)와 달리 `PATCH, DELETE`를 제공안해주기 때문에, controller의 @XXXMapping이 모두 `데이터가 있는 수정은 @PostMapping / 데이터가 없이 id만 넘기는 삭제 요청은 @GetMapping`으로 전해지면서 -> `uri에 /edit, /delete`를 추가로 달고 있다.
- 댓글 view 는 상위도메인인 `게시글 상세보기 view` 내에서
	1. 댓글 목록
	2. 새 댓글
	- 2가지 영역으로 나눠서 만든다.
		![20220519151425](https://raw.githubusercontent.com/is2js/screenshots/main/20220519151425.png)
- 게시글 상세보기는
	1. api#ArticleController로 `localhost:8080/articles/{id}`로 접속하면
	2. articles/show의 `resources/templates/articles/show.mustache`로 화면을 보여준다.
	3. 이제 게시글 상세보기의footer 바로 위에다가 댓글view를 생성해줘야한다.


### 댓글 view 개발 시작
1. 게시글 상세보기(개별조회) view 페이지인 `show.mustache`에서 게시글내용+버튼들보다 더아래 <-> footer위에다가 작성할 준비를 한다.
	- 글 개별조회 `show.mustache`내부에 바로 직접 작성이 아니라, `mustache 삽입형태로 작성`한다.
	```handlebars
	{{>comments/_comments}}
	```

2. _commnets.mustache 역시 삽입의 형태로 2영역으로 나눠준다.
	- **모든 html(mustache) 삽입시 `<div>`로 영역부터 잡아주고 시작한다.**
	- 댓글부터는 mustache파일에 `_`로 시작하게 하고
	- 각 영역을 모두 삽입형태로 만든다.
	```handlebars
	<div>
		<!-- 댓글 목록 _list -->
		{{>comments/_list}}

		<!-- 새 댓글 _new -->
		{{>comments/_new}}
	</div>
	```
	- 매번 실행 -> 에러 -> 파일생성 -> recompile후 재접속하는 형태로 확인해준다.
3. `_list` view 개발
	1. div로 영역을 잡아주되 comments-list라는 id를 준다.
		- 삽입되었지만, 통합되서 보일 때 comments의 list 영역을 명시하기 위함이다.
		```css
		div#comments-list

		<div id="comments-list">

		</div>
		```
	2. 내용물은 응답된 댓글list데이터가 반복될 예정이다.
		- 댓글부터는 apiController에서 javascript로 데이터를 응답받을 예정이다.
		- **list면 `알아서 갯수만큼 반복`되도록 하는 `mustache문법`을 사용해주자**
			- 반복되더라도, 각각이 객체라서 -> 객체속 필드를 중괄호로 뽑아쓰면 된다.
			- `미래에 controller에서 model.addAttribute("commentDtos", )로 model에 담아 응답` -> `{{#commentDtos}}`
		```handlebars
		<div id="comments-list">
			{{#commentDtos}}


			{{/commentDtos}}
		</div>
		```

	3. 반복될 개별 댓글들의 영역도 잡아주되, id로 개별댓글id가 반영되게 한다.
		```handlebars
		<div id="comments-list">
			{{#commentDtos}}
				div#comments-id

				<div id="comments-id"></div>

        		<div id="comments-{{id}}"></div>
			{{/commentDtos}}
		</div>
		```

	4. 개별 댓글마다 부트스트랩-card를 css(class)로 적용해보자.
		![20220519155753](https://raw.githubusercontent.com/is2js/screenshots/main/20220519155753.png)
		1. 댓글영역에 `class="card"`를 추가하고
		2. 더 내부영역에 div.card-header / div.card-body를 준다.
		```handlebars
		3. header에는 nickname을 / body에는 body를 넣어준다.
        <div id="comments-{{id}}" class="card">
            <!--            .card-header-->
            <!--            .card-body-->
            <div class="card-header">{{nickname}}</div>
            <div class="card-body">{{body}}</div>
        </div>
		```

4. `_list` 뷰페이지 -> **상위 게시글 개별조회 controller에서 `하위 댓글데이터`까지 통째로 Model에 담아 뿌리기**
	- 템플릿엔진 Controller에서는 api호출이 아니라 **`부모도메인 controller인 게시글 개별조회(show())`에서 `하위도메인 데이터까지 다 같이 한꺼번에 Model에 담아 view로 응답`시켜줘야한다.**

	- 현재 템플릿엔진Controller는 상위도메인인 Article에 대해서는 service없이 controller에서 repository를 쓰고 있는 상황
	- **하위 도메인은 rest개발과정에서 이미 `상위도메인도 갖다쓰는 메인쉐프 service`를 개발해놨기 때문에, `각 도메인용 repo`가 아닌 `통합repo를 쓰는 하위도메인service`를 통해 하위도메인 `댓글 데이터`를 가져오면 된다.**
		- my) 상위도메인은 단순CRUD -> service없이 repo만 사용했지만
		- **my) 하위도메인은, 상위repo까지 쓰는 복잡 CRUD -> service 개발 -> `하위도메인 데이터 CRUD시 service단위로 시행`**
		```java
		//22-7. 하위도메인의 CRUD는 service단위로 하기 위해 service를 주입한다.
		@Autowired
		private CommentService commentService;

		//22-6. 하위도메인CRUD는 상위repo가 포함된 복잡CRUD라서, repo가 아닌 개발된 service단위로 데이터를 처리한다.
		// 댓글데이터 전체조회(comments) with 상위도메인 id
		final List<CommentDto> commentDtos = commentService.comments(id);

		model.addAttribute("article", articleEntity);
		//22-8. 템플릿 뷰로한꺼번에 넣어주기 위해 하위도메인 dto list도 담아서보낸다.
		model.addAttribute("commentDtos", commentDtos);
		```
	- 이제 서버재시작후 `_list.mustache`가 잘 받아서 반복하여 뿌리는지 확인한다.
		![20220519165140](https://raw.githubusercontent.com/is2js/screenshots/main/20220519165140.png)
	
	- 카드들이 너무 붙어있으니 margin을 class(css)에 `m-2`로 준다.
		```html
		<div id="comments-{{id}}" class="card m-2">
		```

### 큰틀
1. 상위 게시글 개별조회 페이지 맨아래 댓글페이지를 삽입한다.
2. 댓글페이지는 기본영역에 1) 댓글목록 2) 새댓글 2개영역을 삽입한다
3. 댓글목록 페이지는 영역을 가지되 id는 `comments-list`로 준다.
	1. 개별 댓글들은 영역html을 포함해서 mustache 반복문으로 돌린다.
		- 반복문은 dto list가 올 것이다.
		- dto객체들이 변수없이 반복되는데, 그 dto 속 필드둘만 내부에서 뽑아서 사용하면 된다.
	2. 각 댓글들 영역은 `commnets-{{id}}`로 주되, 부트스트랩 card를 활용한다.
		- card는 card-header / card-body가 있다.
4. 템플릿엔진은 1개 컨트롤러 속 mapping메서드내
	- 기존 게시글 개별조회 등 CRUD를 repo로 뽑아와 Model에 넣었었는데
	- 하위도메인인 댓글 CRUD가 상위도메인까지 복잡하여 service단위로 뽑아와 Model에 삽입한다.
	- 게시글 개별조회지만, 댓글은 전체조회로서 dto list를 반환한다.




### 댓글
리스트 머스타치에서 최상위 div에 id="comments-{{id}}" 를 넣은 이유가 뭔가요?

홍팍
2주 전
각 댓글을 구분하기 위함입니다!