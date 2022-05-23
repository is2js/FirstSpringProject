package com.example.firstproject.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    //28-10. 이번엔 만든 어노테이션을 가지고 있는 대상만 해당되도록 포인트컷도 추가해준다.
    // 특정 어노테이션 대상 지정
    @Pointcut("@annotation(com.example.firstproject.annotation.RunningTime)")
    private void enableRunningTime() {
    }

    //28-9.
    // 대상메서드를 지정하는데, 패키지 하위의 모든 메서드 지정은 ..*(모든패키지).*(모든메서드)형태로 지정한다.
    // 기본 패키지의 모든 메소드 지정
    @Pointcut("execution(* com.example.firstproject..*.*(..))")
    private void cut() {
    }

    //28-11. 2개의 포인트컷을 짬뽕해서 로깅메서드를 만든다.
    // -> 실행 시점을 2개의 pointcut을 동시조건을 주면서, before+afterReturning 둘다를 의미하는 @Around 애노테이션을 준다.
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
}
