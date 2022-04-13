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
        3. 
