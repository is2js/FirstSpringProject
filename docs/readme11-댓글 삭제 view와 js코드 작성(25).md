### 기본
- 댓글 수정처럼 댓글 삭제처럼 url에 상위id가 없다.
	- 조회: 상위도메인 아래 묶음으로 하위도메인들 묶음을 조회한다.
	- 생성: 상위id -> 상위entity가 있어야 생성가능하며, 생성시 필드에 상위id도 필요할 것이다.
	- 수정/삭제: 상위id와 상관없이 개별수정/개별삭제만 한다.
- 댓글 삭제도  `게시글 개별조회view > 댓글 전체조회 > 반복문내 개별댓글 조회view` 속에서 버튼으로 존재한다.
	- 댓글 수정 버튼을 댓글 전체조회(`_list`)에 `trigger button  +  modal`로 nickname 옆에 추가해줬었다.
	- **댓글 수정버튼은 `modal의 trigger button`으로서 `id 및 클릭리스너`도 따로 안필요했다. 대신 modal 속 `수정완료`버튼만 `id+클릭리스너`가 달렸었다.**
	- **하지만, 삭제버튼은 반복문내 존재하여 개별반복되고 -> `반복되서 생길 모든 삭제버튼에 클릭리스너`를 달아줘야해서 `id+querySelector`로 변수화하는 것이 아니라 `class + querySelectorAll()`을 활용해서 클릭리스너를 달아줘야하는 점이 다르다.**
		- **마찬가지로 반복되는 `수정modal의 trigger버튼은 알아서 target하여 작동되는 부트스트랩의 기능이 내장`되어있어서 신경쓸 필요 없었다.**
	![20220520182522](https://raw.githubusercontent.com/is2js/screenshots/main/20220520182522.png)

### 댓글삭제 view 작성
1. 삭제버튼 추가 -> 이벤트 리스너 추가까지만 하고 event 확인
	![20220520191042](https://raw.githubusercontent.com/is2js/screenshots/main/20220520191042.png)
	- 여러개를 찾으려고 class selector로 찾았지만 `document.querySelector()`는 가장 먼저 찾은 것만 변수화해 준다. 
		- 2번재 삭제버튼은 작동을 안한다.
		![20220520191153](https://raw.githubusercontent.com/is2js/screenshots/main/20220520191153.png)
	- **class + querySelectorAll로 여러개를 찾았으면, `buttons변수를 반복문을 돌려 -> addEventListener도 매번 달아줘야한다`**
		- buttons변수.forEach( button => { 리스너 달고  작동로직에 log찍기 })
	```js
	<script>
		{
			//(1) 삭제버튼을 변수화해서 가져온다.
			const commentDeleteBtns = document.querySelectorAll(".comment-delete-btn");
			//(2) 삭제 버튼 이벤트 처리
			commentDeleteBtns.forEach(btn => {
				btn.addEventListener("click", () => {
					console.log("삭제 버튼이 클릭되었습니다.");
				});
			});
		}
	</script>
	```
2. `개별id를 id로 안챙기고 &&  class + querySelectorAll로 click리스너를 달아놓은` 삭제버튼을 눌렀을 때 ->`삭제요청을 위해 보낼 id`는 어떻게 js로 수급할 것인가? **삭제버튼의 속성`data-domian-id`로 박아서 받아온다~!**
	- 수정용 modal trigger button 역시 `data-bs-id = {{id}}`의 속성으로 댓글id를 보냈다. **해당 버튼의 직접적인 id가 아니기 때문에 button태그의 id에 댓글id를 챙기지 않았다**
		- **참고로 그 상위 개별댓글영역div에 id가 내포되되도록 작성은 해두었다.**
		![20220520192759](https://raw.githubusercontent.com/is2js/screenshots/main/20220520192759.png)
	- 삭제버튼태그 역시, 해당 버튼태그의 id가 댓글id일 순없으므로 **버틴의 `data-domain-id`라는 속성으로 댓글id값을 담아준다..**
		- CRUD를 시행시킬 버튼에는 기본적으로 `data-domain-id` 속성에 데이터를 넘겨준다.
			- modal trigger 버튼일때만 `data-bs-id`형태로 필드들을 넘겨준다.
		- **수정/삭제 button이 `개별댓글id를 내려받는 개별댓글 구현부`에 배치되는 이유는 data속성으로 id 및 기타 데이터를 받아 넘기기 위해서이다.**
			- **조회는 하위도메인으로서 상위id를 필요로 하며 그것에 대해 묶음으로 조회된다. -> 개별 상위id를 얻을 수 있는 곳은 게시글 개별조회 페이지다!**
		```js
		<button type="button"
				class="btn btn-sm btn-outline-danger comment-delete-btn"
				data-comment-id="{{id}}">
			삭제
		</button>
		```
3. 클릭이벤트 된 `삭제버튼 태그`는 어떻게 받아올까? 삭제버튼을 받아와, 그 내부 `data-comment-id`속성에 박힌 데이터를 가져올 것이다.
	- modal의 경우, boostrap이 정한 event필드를 쓰면 됬었다. event -> event.relatedTarget
		```js
		const triggerBtn = event.relatedTarget;
		```
	- 일반적인 event의 경우, `event.target`를 쓰며 과거엔 `event.srcElement`를 썼다고 한다.
		```js
		const commentDeleteBtn = (event.target) ? event.target : event.srcElement; 
		```
		- event.srcElement는 deprecated되었다고 뜬다.
			![20220520224442](https://raw.githubusercontent.com/is2js/screenshots/main/20220520224442.png)
	- 버튼(event.target)을 받아와, 속성에서 id값을 꺼내고, eventListener만 걸어주고 log찍었듯, log를 찍어준다.
		- js에서는 f-string을 백틱`\` + 달러중괄호`${}`로 사용할 수 있다. **삭제버튼이 해당댓글id를 속성에 담아 잘 가져오는지 확인한다.**
		```js
		btn.addEventListener("click", (event) => {
			//(1) 이벤트 발생 요소(삭제버튼) 선택
			const commentDeleteBtn = (event.target) ? event.target : event.srcElement; 
			//(2) 속성에 박힌 댓글id 가져오기
			const commentId = commentDeleteBtn.getAttribute("data-comment-id");
			//`삭제 버튼 클릭: ${commentId}`
			console.log(`삭제 버튼 클릭: ${commentId}`);
		});
		```
		![20220520230426](https://raw.githubusercontent.com/is2js/screenshots/main/20220520230426.png)

4. forEach -> 개별btn에 클릭이벤트 -> event.target으로 받은 삭제버튼 -> `속성 속 개별 댓글id` -> fetch DELETE 요청하기
	- fetch의 url을 백틱+달러중괄호로 작성할 수 있다.
	- delete는 생성/수정과 달리 보내는 데이터가 없으므로 `body`와 `headers`를 생략하고 method만 기입한다.
	- 생성/수정시에는 `reponse.ok` + `()?:` 삼항연산자를 통해 성공msg/실패msg를 한 줄로 작성했다면
	- 여기서는 `if 실패 -> alert(msg) -> earlyreturn`으로 먼저 반환하고, `그외 성공이라면 다른 로직 수행`할 것이다.
		- 실패: alert("실패msg"); 후 early return
		- 성공: **반복되는 개별댓글 영역 최상위div를 id로 찾아 변수화해서 remove**
	```js
	btn.addEventListener("click", (event) => {
		//(1) 이벤트 발생 요소(삭제버튼) 선택
		const commentDeleteBtn = (event.target) ? event.target : event.srcElement; 

		//(2) 속성에 박힌 댓글id 가져오기
		const commentId = commentDeleteBtn.getAttribute("data-comment-id");
		console.log(`삭제 버튼 클릭: ${commentId}`);

		//25-9. 가져온 id로 삭제요청 하기 by fetch(url, 통신객체블럭{})
		const url = `/api/comments/${commentId}`;
		fetch(url, {
			method: "DELETE"
		}).then(response => {
			if (!response.ok) {
				alert("댓글 삭제 실패!")
				return; 
			}

			// 삭제 실패 응답시 if-early return을 안타는 경우 -> 삭제 성공 응답의 경우

			const target = document.querySelector(`#comments-${commentId}`);
			target.remove(); 
		});
	});
	```
	![20220521004637](https://raw.githubusercontent.com/is2js/screenshots/main/20220521004637.png)
	![20220521004646](https://raw.githubusercontent.com/is2js/screenshots/main/20220521004646.png)

### 큰틀
1. 삭제버튼도 수정버튼(모달트리거버튼)이 있는 개별댓글 영역에 넣어둔다. **버튼 클릭으로 `데이터를 전달`할 때는 속성 `data-domain-id`처럼 속성으로 데이터를 박아둔다.**
2. 삭제버튼처럼 **여러개 반복되서 생성되는 것에 클릭 이벤트**를 걸려면, id+querySelector(첫번째것만 찾음)이 아닌 **`class(domain-delete-btn)`추가 + `querySelectorAll()`**로 이벤트를 건다
3. 삭제버튼들.forEach( 삭제버튼 => {} ) 반복문을 돌려 개별 버튼마다 다시 클릭 이벤트를 거는데
	- **이번엔 이벤트리스너 2번째 인자인 메서드(람다)에서, `event인자`를 줘야 -> 클릭이벤트 대상(삭제버튼)을 `event.target`으로 통째로 받아올 수 있다` -> 속성에서 데이터를 꺼낸다.**
	- 이벤트걸기 + 데이터꺼내기 등 순서대로 매번 log를 찍어보자.
4. 삭제버튼에서 개별댓글id를 꺼냈으면 -> url을 만들고 -> fetch를 보낸다.
	- `url`은 백틱+달러중괄호로 f-string처럼 작성하자
	- delete의 경우 보내는 데이터가 없어서 `통신정보 js객체{}`에 method만 작성해주면 된다.(no body, headers)
5. fetch응답로직인 response => {} 내부에는 **실패시 메세지 alert `earlyreturn`**하고 그외 성공의 경우에는 `삭제할 댓글id` -> `개별댓글영역div의 id`를 만들어 변수로 찾고 -> .remove()로 새로고침없이 실시간으로 삭제한다.

### 댓글
- 전부 훑어봤는데 게시글에 대한 crud는 mvc 모델을 이용, 댓글 기능은 RestAPI & 자바스크립트를 이용한거 같은데 실무에서 백, 프론트 따로 개발한다 했을때 게시글, 댓글 둘다 RestAPI & 자바스크립트 방식으로 구현하는거죠?

홍팍
- 네 맞습니다👍 게시글과 댓글 모두 api방식으로 구현하는게 보통이에요


혹시 게시물 삭제시 댓글도 함께 삭제되게하려면 컨트롤러에서는 서비스 기능을 불러오게만 구현했다면 서비스의 삭제하는 기능을 고쳐야하는게 맞겠죠 ? 
그리고 Article target = articleService.delete(id); 이렇게 담은 target을 commentservice.delete(id); 이렇게 작동시킨뒤 다시 타겟에 저장하는방법은 없을까요?

- 게시글과 함께 댓글도 삭제하려면 **댓글을 먼저 삭제한 다음 게시글을 삭제하면 됩니다.**
	- **ArticleService의 delete 메소드에서 댓글을 먼저 삭제하는 기능을 추가해보세요.**
	- **나중에 JPA의 OneToMany관계설정과 CASCADE를 활용하면
더 쉽게 삭제도 가능합니다👻**
