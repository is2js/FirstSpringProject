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

### 강의

2. view template -> 화면을 담당하는 기술, 웹페이지html를 하나의 틀로 만들고 + 변수를 삽입할 수 있게 하여 -> 변수의 값에 따라 웹페이지가 달라진다.
    1. 여기선 mustache가 뷰 템플릿 엔진으로서 기능을 제공해준다.
        1. template폴더에 .mustache를 생성하는데, 인식을 못하니 플러그인부 설치치
    2. **view template에는 2가지 동료가 있으며, 화면을 볼려면 2가지 동료를 만들어줘야한다.**
        1. 처리:controller
        2. data:model

3. controller는 client로부터 요청을 받는 역할이다. view는 최종페이지를 만들고, 거기에 쓰일데이터들을 model이 만들어준다.
    1. **해석 및 구현 순서: client -> controller 메서드 및 @애너테이션으로 접속요청 + model까지 받아서 -> view 템플릿 페이지에 + 변수값으로 전송 
