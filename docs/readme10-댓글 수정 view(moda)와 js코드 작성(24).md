### 기본
- 댓글 수정 버튼은 `게시글 개별조회view > 댓글 전체조회 > 반복문내 개별댓글 조회view` 속에서 버튼으로 존재한다.
	- 댓글 수정 버튼을 댓글 전체조회(`_list`)에 `trigger button  +  modal`로 추가해준다.
	- nickname 옆에 준다.
### 댓글수정 view 작성


1. bootsrap에서 modal 검색후 `모달 트리거 버튼이 달린 modal`을 live demo에서 복붙해와서 html내부 `댓글 목록 밖 새로운 영역`에 붙여 넣고 확인한다.
	![20220520094304](https://raw.githubusercontent.com/is2js/screenshots/main/20220520094304.png)
	![20220520094405](https://raw.githubusercontent.com/is2js/screenshots/main/20220520094405.png)
	![20220520094422](https://raw.githubusercontent.com/is2js/screenshots/main/20220520094422.png)
	![20220520094429](https://raw.githubusercontent.com/is2js/screenshots/main/20220520094429.png)

2. 반복문 속 `개별 댓글 내부` nickname 옆으로, 댓글 수정을 위한 `modal trigger button`만 잘라내서 가져와 수정한다.
	- **댓글 수정은, 전체조회되는 댓글들 마다 개별로 수정이므로, 반복문 속 돌아가는 개별 댓글마다 수정버튼을 추가해줘야한다.**
	- 모달 태그 자체는 다른영역에서 대기하면 된다.
	- 모달 트리거 버튼의 속성이 많으므로 정렬해준다.
		![20220520094834](https://raw.githubusercontent.com/is2js/screenshots/main/20220520094834.png)
		![20220520094904](https://raw.githubusercontent.com/is2js/screenshots/main/20220520094904.png)
	- 버튼을 확인하고, 버튼을 작게 + outline으로 이쁘게 수정해준다.
		![20220520094959](https://raw.githubusercontent.com/is2js/screenshots/main/20220520094959.png)
		![20220520095009](https://raw.githubusercontent.com/is2js/screenshots/main/20220520095009.png)
		![20220520095021](https://raw.githubusercontent.com/is2js/screenshots/main/20220520095021.png)

3. 이제 `modal`자체도 수정한다. (id 수정 title + body + 불필요 속성 제거)
	1. title 수정
		![20220520095657](https://raw.githubusercontent.com/is2js/screenshots/main/20220520095657.png)
	2. 모달 트리거버튼의 `data-bs-target`속성에 맞춘 id 수정
		![20220520095735](https://raw.githubusercontent.com/is2js/screenshots/main/20220520095735.png)
		![20220520095804](https://raw.githubusercontent.com/is2js/screenshots/main/20220520095804.png)
	3. 필요없는 속성 제거(`aria-~` 2개)
		![20220520095857](https://raw.githubusercontent.com/is2js/screenshots/main/20220520095857.png)
	4. modal-footer 및 내부 버튼 2개 제거 -> 나중에 따로 버튼을 넣을 것이다.
		![20220520095952](https://raw.githubusercontent.com/is2js/screenshots/main/20220520095952.png)
		![20220520100013](https://raw.githubusercontent.com/is2js/screenshots/main/20220520100013.png)

4. modal 내부 `modal-body`부분에 수정form을 작성한다. **생성에 id만 추가하면 되므로 `_new`에서 만든 생성form을 활용(복붙)해서 만든다.**
	1. 생성form을 복붙해서 modal-body영역으로 가져온다.
	2. 생성form을 수정한다.
		1. 주석을 수정form으로 변경
		2. 각 input태그의 id를 포함하여 `new -> edit` / `create -> update`로 변경해준다.
			- new 단어 부분에 ctrl+d를 활용해서 한번에 edit로 변경
				![20220520101000](https://raw.githubusercontent.com/is2js/screenshots/main/20220520101000.png)
			- create 단어 부분에 ctrl+d를 활용해서 한번에 update로 변경
		3. hidden input에 대해 생각해본다.
			- 생성에서는 현재 view(게시글 개별 조회, article)로 내려오는 상위도메인id(article.id)를 fk로 항상 가져가야했다.
			- **수정에서는, url에 상위도메인이 없다. -> 상위id를 이용한 묶음 CRUD가 아니라 개별CRUD이며, 이미 fk로서 상위id를 가진 상태의 데이터를 받는 상태로서, `자신id로만 데이터를 수정`한다.**
				- 현재 view로 내려오는 `상위도메인id(article.id)`를 받아갈 필요없이 현재 댓글데이터 내부의 article_id를 가져가면 된다.
			- **변경 기준인 `자신의 id`까지 항상 가져야하므로 hidden input이 2개다.**
			- **게시글 개별조회에 삽입되는 html로서, 삽입파일 내 보이진 않아도`{{article.id}}`를 사용할 수 있지만, `value로 당장 넣어줄 필요가 없다`**
				- **modal은 반복문 속 개별객체 내부가 아니라 바깥영역에서(반복문 외부) 대기하고 있어서, 돌아가는 가상개별 댓글객체 필드를 뽑아 쓸순 없는 상태다.**
				- **`모달 트리거 버튼`이 작성된 `댓글 목록 반복문 내부` 속 데이터들 -> `바깥영역에서 대기`중인  modal-body form 각 input에다가 수정전 값을 던져줘야할 것이다.**
			![20220520102118](https://raw.githubusercontent.com/is2js/screenshots/main/20220520102118.png)
		![20220520102132](https://raw.githubusercontent.com/is2js/screenshots/main/20220520102132.png)
5. **데이터가 존재하는 곳(반복문 내부 개별댓글)에 있는 `모달 트리거 버튼` -> 바깥에서 대기하는 `모달`로 데이터를 `js`로 던져주는 boostrap modal 파악**
	1. boostrap modal 문서에서 `Varying modal content` 부분을 확인하여, 데이터를 받아오는 modal을 확인한다.
		![20220520102557](https://raw.githubusercontent.com/is2js/screenshots/main/20220520102557.png)
		![20220520102650](https://raw.githubusercontent.com/is2js/screenshots/main/20220520102650.png)
	2. 트리거버튼의 속성과 modal관련 javascript예제를 확인해서 구조를 파악한다.
		1. 트리거버튼의 속성(`data-bs-*`)으로 필드마다 데이터를 담아둔다.
		2. modal을 js로 변수화 -> modal에 모달 열리는 것을 인지하는 addEventListner -> event 발생시 트리거버튼 변수화 -> 트리거버튼 속성의 데이터를 가져온다
		3. modal 내부 input태그들 변수화 -> 데이터 .value에 삽입
		![20220520102841](https://raw.githubusercontent.com/is2js/screenshots/main/20220520102841.png)
		![20220520102939](https://raw.githubusercontent.com/is2js/screenshots/main/20220520102939.png)
			
6. **트리거버튼에 `수정전 개별댓글 데이터`를 `data-bs-*` 속성으로 심어준다.**
	1. **댓글list(`_list`) 속 반복문 내 `현재 개별댓글 데이터`를, 모달 트리거 버튼의 `data-bs-*` 속성에 각 필드데이터 담기**
		- data-bs-toggle과 data-bs-target(대기중 모달) 밑으로 속성으로 데이터를 심는다.
		```html
		<!-- data-bs-toggle과 data-bs-target(대기중 모달) 밑으로 속성으로 modal로 보내줄 데이터들을 심는다. -->
		<button type="button"
				class="btn btn-outline-primary btn-sm"
				data-bs-toggle="modal"
				data-bs-target="#comment-edit-modal"
				data-bs-id="{{id}}"
				data-bs-nickname="{{nickname}}"
				data-bs-body="{{body}}"
				data-bs-article-id="{{articleId}}">
			수정
		</button>
		```
5. scrip태그를 만들어 모달트리거버튼 속성 속 데이터를 `modal이 트리거버튼이 작동하는 "show.bs.modal" event를 감지`하여 수정form의 input들로 값을 넘겨주도록 javascript 코드를 짠다.
	1. modal을 변수화하고, 모달트리거버튼이 열림 event를 `show.bs.modal`이벤트로 감지한다. **이 때 데이터를 받기 위해 `event`를 인자로 받아 로직을 수행한다.**
		```js
		<script>
			{
				//(1) 데이터를 받을 modal 변수화
				const commentEditModal = document.querySelector("#comment-edit-modal");
				//(2) 변수화 이후에는, 이벤트감지
				commentEditModal.addEventListener("show.bs.modal", function (event) {
					console.log("모달이 열렸습니다");
				})
			}
		</script>
		```
	2. event를 잘 감지하는지 콘솔로 찍어 확인한다.
		![20220520131117](https://raw.githubusercontent.com/is2js/screenshots/main/20220520131117.png)
	3. boostrap 문서를 보면, `모달트리거버튼`을 `event.relatedTarget`으로 변수화해서 통째로 받을 수 있다고 한다.
		![20220520130802](https://raw.githubusercontent.com/is2js/screenshots/main/20220520130802.png)
		- 부트스트랩용 속성이라서 자동완성은 안된다.
		```js
		<script>
			{
				//(1) 데이터를 받을 modal 변수화
				const commentEditModal = document.querySelector("#comment-edit-modal");
				//(2) 변수화 이후에는, 이벤트감지
				commentEditModal.addEventListener("show.bs.modal", function (event) {
					//console.log("모달이 열렸습니다");
					console.log(event.relatedTarget);
				})
			}
		</script>
		```
		![20220520131420](https://raw.githubusercontent.com/is2js/screenshots/main/20220520131420.png)

	4. 모달트리거버튼에서 하나씩 데이터를 꺼내야하니 변수화하고 -> getAttribute("")로 속성에 박힌 데이터를 꺼내서 변수로 받아준다.
		- 꺼낸 데이터를, modal 속 수정form의 각 input에 value로 넣어준다.
		```js
		commentEditModal.addEventListener("show.bs.modal", function (event) {
		// 받은 모달 트리거 버튼 변수화
		const triggerBtn = event.relatedTarget;
		// 모달 트리거 버튼 속성 데이터 가져오기
		const id = triggerBtn.getAttribute("data-bs-id");
		const articleId = triggerBtn.getAttribute("data-bs-article-id");
		const nickname = triggerBtn.getAttribute("data-bs-nickname");
		const body = triggerBtn.getAttribute("data-bs-body");

		//데이터를 modal 속 수정form에 입력하기 by id+querySelector
		document.querySelector("#edit-comment-id").value = id;
		document.querySelector("#edit-comment-article-id").value = articleId;
		document.querySelector("#edit-comment-nickname").value = nickname;
		document.querySelector("#edit-comment-body").value = body;
        });
		```
		![20220520135610](https://raw.githubusercontent.com/is2js/screenshots/main/20220520135610.png)

	5. modal 속 수정form을 우클릭>검사해서 데이터 value가 잘 박혀있는지 확인할 수 있다.
		- **form에서 바로 보이지 않는 hidden input에 박아준 데이터가 `value=""`로 잘 들어가 있는지 확인한다.**
		![20220520140126](https://raw.githubusercontent.com/is2js/screenshots/main/20220520140126.png)

6. script태그 속 새 블락에 수정form 속 `수정완료 button`를 `변수화` -> `클릭이벤트 감지` -> `데이터를 js객체`에 모으고 -> `fetch`를 통해 restAPI 수정 요청을 보낸다.
	- 클릭이벤트를 달아주려면, id+querySelector로 변수화부터 해준다.
	- fetch를 보내기 전, `요청 데이터들은 js객체`로 만들어놓아야, json으로 만들 수 있다. 
		- **요청데이터들을 모아야 script의 격리된 영역에서 fetch용 url에 현재 댓글 id를 넣어줄 수 있다.**
		- 데이터들을 모을 땐, 미리 필드들을 모아 틀을 작성해놓자.
		- **사용자 수정가능성 이후, 수정form에 박힌 새로운 value들을 다시 한번 id + querySelector로 뽑아와야하기 때문에, 단순 value가 아니므로 미리 틀을 작성해놓는 버릇을 들이자**
			![20220520142422](https://raw.githubusercontent.com/is2js/screenshots/main/20220520142422.png)
		- fetch보내기전, 버튼클릭이벤트로 `만들어진 js객체는 한번 찍어본다.`
			![20220520142550](https://raw.githubusercontent.com/is2js/screenshots/main/20220520142550.png)
	- fetch를 응답로직 작성전, **요청까지 작성하고 `spring 로그` 및 `실제 db`를 확인한다.**
		![20220520143957](https://raw.githubusercontent.com/is2js/screenshots/main/20220520143957.png)
		![20220520144013](https://raw.githubusercontent.com/is2js/screenshots/main/20220520144013.png)
		![20220520144036](https://raw.githubusercontent.com/is2js/screenshots/main/20220520144036.png)
	- fetch 응답은 `function(response) { 로직 }`로 함수를 실행시켜 로직을 수행하지만, `람다식 response => {} `로 줄인 것이다.
		1. 응답로직에는 response.ok + 삼항연산자를 활용한 msg alert
		2. 새로고침( 생성시와 동일)
		![20220520144449](https://raw.githubusercontent.com/is2js/screenshots/main/20220520144449.png)
	```js
	// 24-8. 수정완료 버튼 변수화 -> 클릭이벤트 감지 -> restAPI에 수정 요청한다.
    {
        // 수정완료버튼 변수화
        const commentUpdateBtn = document.querySelector("#comment-update-btn");

        // 클릭 이벤트 감지 및 fetch요청
        // - click event는 데이터를 받아올 일이 없어서 function() {}의 event인자를 줘도 되고 안줘도 된다.
        commentUpdateBtn.addEventListener("click", function (event) {
            //fetch전, 수정 요청 댓글 데이터 js객체 생성
            const comment = {
                id: document.querySelector("#edit-comment-id").value,
                article_id: document.querySelector("#edit-comment-article-id").value,
                nickname: document.querySelector("#edit-comment-nickname").value,
                body: document.querySelector("#edit-comment-body").value
            }
            //console.log(comment);

            // 수정 REST API 호출 by fetch() -> url, 통신js객체 블록{}
            const url = "/api/comments/" + comment.id;
            fetch(url, {
                method: "PATCH", // PATCH요청 소문자 쓰면 안됨
                body: JSON.stringify(comment), // js객체 -> JSON으로 전달
                headers: {
                    "Content-Type": "application/json"
                }
                //}); // 요청까지만 작성하고, 응답 로직 작성 전, log + 실db를 확인한다.
                // 요청응답이 왔을 때, `function(response) { 로직 }`로 함수를 실행시켜 로직을 수행하지만, `람다식 response => {} `로 줄인 것이다.
            }).then(response => {
                //http 응답 코드에 따른 메세지 출력
                const msg = (response.ok) ? "댓글이 수정되었습니다." : "댓글 수정 실패!";
                alert(msg);
                //새로고침
                window.location.reload();
            })

        });
    }
	```
	
### 큰틀
1. 댓글 수정 버튼은, `게시글 개별조회 > 댓글 전체조회 > 반복문 속 개별 댓글`마다 달리게 된다.

2. 댓글 수정 버튼은 boostrap modal의 `modal trigger button` 코드를 사용하고, 열릴 `modal`은 다른 영역에서 대기하고 있는다.

3. 댓글 생성form을 복사해와서 modal-body에 넣어 댓글 `수정form`을 만든다.
	- ctrl+d를 활용해 각종 id를 new -> edit / create -> update로 수정해주자.
	- `생성`시 게시글 개별조회에 삽입되는 상태라 `{{article.id}}`로 템플릿변수를 바로 사용해서, `hidden input`에 현재 게시글(상위id)변수를 `value 속성을 미리 넣어`뒀다면
	- **`수정`시에는 수정form이 게시글의 바깥영역에 대기중인 modal에 위치하기도 하며, `현재 댓글데이터의 흐름`이 아래와 같아서 `수정form 속 hidden input 미리 value`를 넣어놓지 않는다.**
		1. 생성시와 달리 수정시에는 `상위id` 뿐만 아니라 `하위id(댓글 자신id)`또한 hidden input으로서 달고 있는다.
			- 생성은 상위id(fk)를 입력없이 항상 단 체로 생성된다.
			- 수정은 상위id(fk)뿐만 아니라 수정시 자신의id인 하위id 또한 필요하다.
4. 이후 데이터의 흐름
	1. controller -> 댓글dto list -> 반복문내 댓글dto 필드들
	2. **댓글dto필드들 -> 모달트리거버튼 속성들(`data-bs-*`) -> modal show.bs.modal event -> 모달트리거버튼 받아오기 -> `수정form 각 input들.value = `에 박아주기**
		- **여기서 `hidden input.value = `에도 박아주기 때문에, `hidden input2개가 미리 default value를 지정해줄 필요가 없다`.**
	3. **사용자 수정modal 열기 -> 사용자 수정 -> 수정완료버튼 클릭리스너 -> js객체 -> json -> fetch**

### 댓글