### 기본
- 새 게시글은 `html form` 요청 -> `템플릿엔진 controller`에서 처리했었다.
	- 게시글 개별조회에서 댓글 목록(전체조회)도 템플릿엔진 controller에서 해서, 게시글 개별조회 + 댓글 전체조회를 한꺼번에 받아왔다.
- 하지만, 새 댓글은 `js` 요청 -> `RestController`에서 처리 -> `js가 다시 응답받아 동적으로 처리`까지 한다.
	- 하위도메인의 전체 조회는 정적으로 해도되는데
	- **하위도메인 개별생성/조회/수정 등은 js + restAPI로 동적으로 해야한다.**
- 처리 흐름
	1. `_new` 페이지 및 요청 버튼 작성
	2. js요청 -> (restAPI처리) ->  js응답후 처리까지 작성

- 버튼과 javascript
	1. 버튼 변수화 -> document.querySelector
	2. 클릭시 이벤트 ->  addEventListener
	3. 이벤트로서 restAPI 호출 -> fetch API
	4. 댓글정보를 javascript객체로 -> const = {}
	5. js객체를 json으로 -> JSON.stringify
	![20220519171158](https://raw.githubusercontent.com/is2js/screenshots/main/20220519171158.png)
	![20220519171331](https://raw.githubusercontent.com/is2js/screenshots/main/20220519171331.png)

### 댓글생성 view 작성
1. `_new.mustache`에서 통합시 보일 영역을 id `comments-new` + css로 부트스트랩 `card` + `m-2`를 활용한다.
	- card는 card-header없이 card-body만 사용하자.
	```css
	div#comments-new.card.m-2

	div.card-body
	```
2. 댓글생성은 talendAPI에 넣었던 것처럼 **`데이터를 전달`해야하므로 `form 틀`을 이용**하는 것은 게시글 생성과 매한가지이나 **`form자체의 method + action은 사용하지 않고`, js로 대체한다.**
	```html
	<form action=""></form>
	<!-- action 속성 삭제된 form. 폼은 데이터전달의 틀로만 사용한다. -->
	<form></form>
	```
	- form의 사용은 부트스트랩 홈페이지를 참고하되
		- 각 데이터필드별 만들고 -> recompile하면서 확인한다.
	- `<form>태그` action없는  form 
		- string(nickname) -> `input`
			- `<div>` div.mb-3
				- `<label>` label  .form-label
				- `<input>` input  `#new-comment-필드명`  .form-control.form-control-sm
			```css
			div.mb-3>label[for="new-comment-nickname"].form-label{nickname}+input#new-comment-nickname.form-control.form-control-sm
			```
			![20220519175713](https://raw.githubusercontent.com/is2js/screenshots/main/20220519175713.png)
		
		- string(body) -> `textarea`
			- `<div>` div.mb-3
				- `<label>` label  .form-label
				- `<input>` textarea[rows=3]  `#new-comment-필드명`  .form-control.form-control.sm 

			```css
			div.mb-3>label[for="new-comment-body"].form-label{body}+textarea[rows=3]#new-comment-body.form-control.form-control-sm
			```
			- 지금까지보면, label+input 둘다 있는 것들만 div로 주고
				- 앞으로 label없는 hidden input이나, button은 따로 안준다.
		- hidden input(현재 게시글id인 상위도메인id) -> `input[type="hidden"]` 
			- 입력없이 항상 가지고 있어야할 값은 hidden
			- div없이 바로 input[type="hidden"]만 가진다.
			```css
			input#new-comment-$domains$-id[type="hidden",value="{{$domains$.id}}"]
			```
		- button (no action submit)
			```css
			button#comment-create-btn.btn.btn-outline-primary.btn-sm[type="button"]{댓글작성}
			```
			![20220519205225](https://raw.githubusercontent.com/is2js/screenshots/main/20220519205225.png)

3. action, method없는 form의 button에 요청 event달기
	- javsript코드는 `_new.mustache` 내부 맨 아래서 script태그로 작성한다.	
		![20220519220259](https://raw.githubusercontent.com/is2js/screenshots/main/20220519220259.png)
	1. `#id` + querySelector로 `form속 button태그를 변수화`해서 가져온다
		```js
		const commentCreateBtn = document.querySelector("#comment-create-btn");
		```
	2. 변수화된 button태그에 `"click"` 이벤트를 감지하여, 함수를 실행시켜주는 `addEventListener`를 달아주고, 내부에서 `Javascript객체(json 전)`을 임의로 만들어서 반환해본다
		1. 이벤트리스너는 이벤트종류를 선택 + 수행할 로직도 함수로 적어준다.
			```js
			commentCreateBtn.addEventListener("click", function () {
				console.log("버튼이 클릭되었습니다.")
			});
			```
			- 달고 log로 찍어서 -> 크롬 개발자도구>콘솔 ctrl+shift+i(f12)에서 확인한다.
		2. `javascript객체`를 key는 dto에 들어갈 field / `value값은 "???"`로 만들어서 반환 확인해본다.
			- **json과 달리 javascript객체는 key값에 따옴표가 없다!!**
			- 생성이니까 자신의id를 제외하고 필드를 만들어준다.
			```js
			commentCreateBtn.addEventListener("click", function () {
				console.log("버튼이 클릭되었습니다.")
				
				//23-5. json과 달리 javascript객체는 key값에 따옴표가 없다!!
				const comment = {
					nickname: "???",
					body: "???",
					article_id: "???"
				}
				console.log(comment);
			});
			```
			![20220519224022](https://raw.githubusercontent.com/is2js/screenshots/main/20220519224022.png)
				- **restAPI의 dto에서는 `articleId`를 사용하되 `@JsonProperty`를 통해 `article_id`로 json이 들어온다고 명시해줬다. -> `java랑 동일하게 articleId로 던지면 명시된 것과 달라서 에러난다. 자동으로 안잡아준다.`**
				- **js에도 camelCase를 쓰지만, `api로 데이터 던질 땐 snake_case로 많이 작성`하니 잘 알아두자.**
		3. javascript객체 속 value값을 
			1. `form 속의 input들 id` + `querySelector`로 태그를 변수화하고
			2. **input태그에 `사용자가 입력한 value`** or **hiddenInput태그 속 `속성으로 준 value`**를 `변수화한태그.value`로 가져올 수 있다.
			```js
			commentCreateBtn.addEventListener("click", function () {
				console.log("버튼이 클릭되었습니다.")

				//23-5. json과 달리 javascript객체는 key값에 따옴표가 없다!!
				const comment = {
					// nickname: "???",
					nickname: document.querySelector("#new-comment-nickname").value,
					// body: "???",
					body: document.querySelector("#new-comment-body").value,
					// article_id: "???"
					article_id: document.querySelector("#new-comment-article-id").value
				}
				console.log(comment);
			});
			```
			![20220519224753](https://raw.githubusercontent.com/is2js/screenshots/main/20220519224753.png)
			![20220519224827](https://raw.githubusercontent.com/is2js/screenshots/main/20220519224827.png)
	3. `fetch(url, {통신정보js객체})`를 통해, talendAPI처럼 생성요청을 보내보자.
		1. 첫번째 인자로 필요한 url을 생성 route에 보낸다
		2. 2번째 인자로 통신정보를 js객체로 짠다.
			```js
            const url = "/api/articles/" + comment.article_id + "/comments";
            fetch(url, {
                // {}은 js객체라 key에 따옴표가 없다.
                method: "post",                 // HTTP 요청 method
                body: JSON.stringify(comment),  // js객체 to json을 요청body로
                headers: {
                    "Content-Type": "application/json" // header가 아니라 headers이며, string을 key로 가지는 객체로 정보전달(not js객체)
                }
            });
			```
	4. fetch성공여부는 1. 개발자도구 2. DB에서 생성됬는지 응답을 확인한다.
		![20220519230940](https://raw.githubusercontent.com/is2js/screenshots/main/20220519230940.png)
		![20220519230922](https://raw.githubusercontent.com/is2js/screenshots/main/20220519230922.png)
	5. fetch 자체의 응답로직을 `.then(response => {  response로 수행할 작업 });`을 작성한다.
		1. response는 응답이 제대로 왔는지 확인할 수있는 boolean의  `response.ok`가 있다. 이것을 활용하면 `if가 내장된 삼항연산자`로 구분지어서 msg를 작성할 수 있다.
		    -  삼항연산자 + response.ok에 따라 생성된 응답메세지를 alert()로 띄운다
			![20220520004250](https://raw.githubusercontent.com/is2js/screenshots/main/20220520004250.png)
		2. 생성요청을 하면, 성공/실패여부를 떠나서 `새로고침`해준다.
		
			```js
            const url = "/api/articles/" + comment.article_id + "/comments";
            fetch(url, {
                method: "post",                 // HTTP 요청 method
                body: JSON.stringify(comment),  // js객체 to json을 요청body로
                headers: {
                }
            }).then(response => {
                // (1) http 응답 코드에 따른 삼항연산자로 ok vs 그외의 경우를 (response.ok? "okmsg" : "badmsg" )로 출력할 메세지 작성 후 alert
                const msg = (response.ok) ? "댓글이 등록되었습니다." : "댓글 등록 실패!";
                alert(msg);
                // (2) 생성 성공or실패 상관없이 [생성요청후 새로고침] (생성 완료 후 새로고침하면 -> 댓글 전체조회시 추가되어 표기됨)
                window.location.reload();
            });
			```







### 큰틀
1. _comments > _list + _new 중에 `_new`의 form을 작성하는데
	1. 영역을 id + 부트스트랩 card로 준다
	2. submit버튼 대신 js로 요청하기 위해 `action, method없는 form태그` 내부에
		1. label + input/textarea 등 조합 + hidden input + 링크없는 button을 만든다.
	2. 맨아래 script태그 내부에, button을 변수화 -> click event -> fetch를 통해 생성요청하도록 한다.
		1. 버튼 변수화 -> event -> fetch
		2. fetch는
			1. url먼저 작성
			2. 통신정보 js객체 작성
			3. 응답로직 작성
				1. 성공/실패여부 msg -> alert
				2. 새로고침



### 댓글

안녕하세요. 혹시 script태그 안에서 { } block을 잡고 시작한 이유를 알 수 있을까용??


홍팍
1시간 전
- 큰 의미는 없구, 방어적으로 스코프를 제한했다 정도로 보심 되겠습니다.