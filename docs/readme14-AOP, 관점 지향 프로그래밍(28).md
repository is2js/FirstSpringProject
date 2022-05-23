### 기본
- AOP: 관점지향(Aspect Oriented) 프로그래밍(Programming) 
	- **기존 핵심적인 관점을 두고, `부가적인 관점을 따로 나누어서 처리`**
- CRUD 기능 이외에 부가적인 코드도 작성되는데?
	- parameter 로깅
		![20220521163223](https://raw.githubusercontent.com/is2js/screenshots/main/20220521163223.png)
	- return value 응답 로깅
		![20220521163238](https://raw.githubusercontent.com/is2js/screenshots/main/20220521163238.png)
	- 퍼포먼스 수행시간 로깅
		![20220521163258](https://raw.githubusercontent.com/is2js/screenshots/main/20220521163258.png)

	- 은행업무의 부가기능
		![20220521163324](https://raw.githubusercontent.com/is2js/screenshots/main/20220521163324.png)

- 문제점: 부가 기능들은 핵심로직마다 반복 작성된다.
	![20220521163347](https://raw.githubusercontent.com/is2js/screenshots/main/20220521163347.png)

- 해결책: AOP기법
	- 부가기능을 특정지점에 잘라넣는 기법
	- `DI`: 특정 `객체` 주입
	- `AOP`: 특정 `부가기능 로직`을 주입
	![20220521163525](https://raw.githubusercontent.com/is2js/screenshots/main/20220521163525.png)

	- 예시: @Transactional -> 실패시 롤백 로직을 주입

- AOP 주요 어노테이션 종류
	![20220521163713](https://raw.githubusercontent.com/is2js/screenshots/main/20220521163713.png)


### 실습
#### 댓글 service  입/출력값 확인을 위한 `logging AOP`작성해보기 

1. `application.properties`에서 실제db 세팅을 -> h2 메모리db로 바꾸고 실습한다.

#### 댓글 생성 service 입출력 로깅

- controller - service- repository의 과정 중 `service`에 대한 입출력(parameters, return value)를 로깅해보자.
	![20220521171628](https://raw.githubusercontent.com/is2js/screenshots/main/20220521171628.png)

##### AOP 전, lombok - @Slf4j를 통해 로그 찍기(문제점)
1. CommentService에 `@Slf4j` 를 달아 
2. CommentService의 create메서드로 가서 log.info()로 입력값2개, 반환값 1개를 찍어본다.
	```java
	@Transactional
    public CommentDto create(final Long articleId, final CommentDto commentDto) {
        // AOP 도입 전 lombok의 @Slf4j 로그찍기
        // - lombok의 log.info는 콤마로 formatting 출력 가능
        log.info("입력값 => {}", articleId);
        log.info("입력값 => {}", commentDto);

        final Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패! 대상 게시글이 없습니다."));

        final Comment comment = commentDto.toEntity(article);

        final Comment created = commentRepository.save(comment);

        final CommentDto createDto = CommentDto.createCommentDto(created);
        // AOP 도입 전 lombok의 @Slf4j 로그찍기
        log.info("반환값 => {}", createDto);
        return createDto;
    }
	```
	![20220521174723](https://raw.githubusercontent.com/is2js/screenshots/main/20220521174723.png)

3. log.info()로 입출력값 로깅시 문제점
	1. **핵심 로직이 아닌 부가적인 로깅코드가 코드로 포함됨**
	2. log.info() 찍는 **코드는 반복됨**

4. 해결책: `로깅 AOP 적용`

##### AOP를 통한 입력값(args) log 찍는 클래스 생성

1. `aop`(부가기능 주입) 패키지 생성
2. 디버깅 부가기능을 위한 `DebuggingAspect` 클래스 생성
3. aspect 어노테이션 3개 달아주기
	```java
	@Aspect // AOP 클래스 선언: 부가기능 주입(AOP) 클래스 명시
	@Component // 주입시키려면 class 객체를 생성해야 -> @Autoweird로 주입하는데, IoC Container에게 객체 생성을 맡긴다
	@Slf4j // 로그찍는 기능을 여기다가 넣기 위해 추가
	public class DebuggingAspect {
	```
4. 대상 메서드 지정 메서드 정의해주기
	```java
	@Pointcut("execution(* com.example.firstproject.service.CommentService.create(..))")
    private void cut() {
    }
	```
5. @Before + JoinPoint를 활용해, 입력값 로깅 메서드 작성
	```java
	//28-3. 삽입할 내용(1) @Before : 메서드 실행 이전에 부가기능 실행
    // cut()의 대상이 수행되기 이전에 수행
    @Before("cut()")
    //joinpoint: cut()의 대상메서드
    public void loggingArgs(final JoinPoint joinPoint) {
        //28-4. 할일 정리
        // 입력값 가져오기
        //클래스명 from Target class 추출 (로그에 찍어서 넣어줄)
        //메서드명 from Signature 추출 (로그에 찍어서 넣어줄)
        //입력값 로깅하기 - args 반복문으로 돌면서 log.info()찍기
        // CommentService#create()의 입력값 => 5
        // CommentService#create()의 입력값 => CommentDto(id=null...)

        //입력값 가져오기
        final Object[] args = joinPoint.getArgs();

        //클래스명
        final String className = joinPoint.getTarget()
            .getClass()
            .getSimpleName();
        //메서드명
        final String methodName = joinPoint.getSignature()
            .getName();

        //입력값 로깅하기
        for (final Object obj : args) {
            log.info("{}#{}의 입력값 => {} ", className, methodName, obj);
        }

        //28-5. 서버 재실행해서 이것들이 찍히는지 확인하기
    }
	```

6. 서버 재실행해서 확인하기
	![20220522091910](https://raw.githubusercontent.com/is2js/screenshots/main/20220522091910.png)


##### AOP를 통한 반환값(ReturnValue) log 찍는 클래스 생성
1. @Before와 반대되는 것은 @After가 아닌 @AfterReturning 어노테이션이다.
	```java
    // 28-6. cut()에 지정된 대상메서드 호출 성공후 실행
    //      -> 파라미터로 지정할 return값(returnObj) 변수명을 따로 지정해줘야한다.
    @AfterReturning(value = "cut()", returning = "returnObj")
    public void loggingReturnValue(final JoinPoint joinPoint, // cut()의 대상메서드
                                   final Object returnObj) { // 리턴값을 Object로

        // 반환값을 따로 변수로 안받아와도 되고, 파라미터 + @애노테이션 지정만 해주면 파라미터에 응답값이 들어오게 된다.

        //클래스명
        final String className = joinPoint.getTarget()
            .getClass()
            .getSimpleName();
        //메서드명
        final String methodName = joinPoint.getSignature()
            .getName();

        // 반환값 로깅
        // CommentService#create()의 입력값 => CommentDto(id=10, ..)
        log.info("{}#{}의 반환값 => {} ", className, methodName, returnObj);
    }
	```


2. 서버 재실행해서 확인하기
	![20220522093102](https://raw.githubusercontent.com/is2js/screenshots/main/20220522093102.png)

##### aop 대상 메서드를 특정메서드 -> 해당 클래스내 전체 메서드로 변환하기
1. 현재는 댓글 생성대신 수정하면, 값이 안찍힘. 모든 메서드에 적용하려면 @PointCut의 메서드를 \*로 대체한다.
	```java
	@Pointcut("execution(* com.example.firstproject.service.CommentService.*(..))")
	```
	![20220522094828](https://raw.githubusercontent.com/is2js/screenshots/main/20220522094828.png)


##### `수행시간 측정 AOP` 작성
1. aop패키지가 아닌 `annotation`패키지를 추가한다.
2. class를 `RunningTime`이라는 어노테이션 클래스를 만들어준다.
    - **수행시간 측정 aop은 모든 메서드에 적용되도록 할 것인데, 모든 메서드 중에서도 `@RunningTime`애노테이션 단 것만 측정하도록 `추가 조건을 주기 위해 만드는 애노테이션`이다.**
	![20220522100133](https://raw.githubusercontent.com/is2js/screenshots/main/20220522100133.png)
	![20220522100138](https://raw.githubusercontent.com/is2js/screenshots/main/20220522100138.png)
3. 어노테이션 타겟을 @Target으로 지정해주되, 중괄호+콤마로 TYPE과 METHOD를 명시해놓는다.
	![20220522100230](https://raw.githubusercontent.com/is2js/screenshots/main/20220522100230.png)
4. 어노테이션 유지기간을 @Retention으로 지정해주되, RUNTIME시점만 적용한다.
	```java
	@Target({ElementType.TYPE, ElementType.METHOD}) // 어노테이션 대상 지정
	@Retention(RetentionPolicy.RUNTIME) // 어노테이션 유지기간
	public @interface RunningTime {
	}
	```

5. 이제 `aop`패키지로 와서 `PerformanceAspect` 클래스를 생성하고
	1. aop클래스로 선언하기 위해 애너테이션을 달아준다.
		![20220522100511](https://raw.githubusercontent.com/is2js/screenshots/main/20220522100511.png)	
	2. 포인트컷으로 aop 적용 대상을 지정해준다.
		- 메서드부터 만들어야지 @PointCut execu~가 자동완성 됨.
        - 하위 모든 패키지는 `..*`을, 모든 메서드는 `.*`을 모든 파라미터는 `(..)`으로 지정해준다
        ```java
        // 대상메서드를 지정하는데, 패키지 하위의 모든 메서드 지정은 ..*(모든패키지).*(모든메서드)형태로 지정한다.
        // 기본 패키지의 모든 메소드 지정
        @Pointcut("execution(* com.example.firstproject..*.*(..))")
        private void cut() {
        }
        ```
    3. 기본 패키지 하위 모든 메서드를 적용대상으로 하고, **추가 발동  조건을 주기 위해 @PointCut을 추가하는데, 애노테이션을 지정해준다.**
        ```java
        // 특정 어노테이션 대상 지정
        @Pointcut("@annotation(com.example.firstproject.annotation.RunningTime)")
        private void enableRunningTime() {
        }
        ```
    4. 수행시간은 @Before + @After 전체에 대해 적용해야하니 `@Around`애노테이션을 활용하고 발동 조건으로 pointcut메서드인 `cut()`과 추가 발동 조건인 `enableRunningTime()`의 2개 모두 지정해준다.
        - 이 때, 타겟 메서드를 실행까지 시켜야하므로 jointPoint가 아닌 `ProceedingJoinPoint`를 사용한다.
        ```java
        @Around("cut() && enableRunningTime()")
        public void loggingRunningTime(final ProceedingJoinPoint joinPoint) {}
        ```
    5. 이제 스프링이 제공하는 `StopWatch`를 활용해서, 메서드 수행의 시간을 제고, 로그로 찍어준다.
        ```java
        // 2 조건을 모두 만족하는 대상에 대해 [전후로 부가기능을 삽입]
        //  - 이때는 joinpoint가 대상 실행을 할 수 있는 다른 ProceedingJoinPoint로 준다.
        @Around("cut() && enableRunningTime()")
        public void loggingRunningTime(final ProceedingJoinPoint joinPoint) throws Throwable {
            //28-12. 할일
            // 메소드 수행 전, 측정 시작
            // 메소드 수행
            // 메서드 수행 후, 측정 종료 및 로깅

            //28-14. 시간 측정을 위해 스프링이 제공하는 StopWatch()를 사용한다.
            // 메소드 수행 전, 측정 시작
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            //28-13.
            // 메소드 수행
            final Object returningObj = joinPoint.proceed(); // 타겟을 실행 -> 에러는 예외를 밖으로 던져주기

            // 28-15.
            // 메서드 수행 후, 측정 종료 및 로깅
            stopWatch.stop();
            // 메서드명
            final String methodName = joinPoint.getSignature()
                .getName();
            log.info("{}의 총 수행 시간 => {} sec", methodName, stopWatch.getTotalTimeSeconds());

            //이제 cut에 지정된 메서드이면서 && RunningTime 어노테이션을 가지고 있는 놈들에 대해 시간 측정한다.
            // -> 댓글 삭제 controller에 @RunningTime을 붙여 확인해본다.
            // -> cut에 지정된 메서드는 알아서 현재 로깅이 작동되니, RunningTime만 붙여준다.
        }
        ```


6. 대상 메서드(댓글 삭제 Controller)에 추가 발동조건인 `@RunningTime` 애노테이션을 달아주고, 서버 재시작후 잘 수행시간 측정이 제대로 찍히는지 확인한다.
    ![20220523091315](https://raw.githubusercontent.com/is2js/screenshots/main/20220523091315.png)
