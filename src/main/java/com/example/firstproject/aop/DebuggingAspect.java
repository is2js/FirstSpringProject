package com.example.firstproject.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//28-1. 
@Aspect // AOP 클래스 선언: 부가기능 주입(AOP) 클래스 명시
@Component // 주입시키려면 class 객체를 생성해야 -> @Autoweird로 주입하는데, IoC Container에게 객체 생성을 맡긴다
@Slf4j // 로그찍는 기능을 여기다가 넣기 위해 추가
public class DebuggingAspect {

    //28-2. 대상 메서드: 어느 메서드를 짤라서 들어가느냐 == 어느 지점에 주입할거냐를 정해주는 메서드 생성
    // 대상 메서드 선택: CommentService#create() 메서드에 주입할 것임
    // - *는 접근제어자 + return값을 대신함 / (..)는 메서드 인자 모든 것을 의미함
    //28-8. create이외 모든 메서드에 적용하기 위해 메서드명을 * 로 수정한다.
//    @Pointcut("execution(* com.example.firstproject.service.CommentService.create(..))")
//    @Pointcut("execution(* com.example.firstproject.service.CommentService.*(..))")
    // 모든 service의 메소드 적용하기
//    @Pointcut("execution(* com.example.firstproject.service.*Service.*(..))")
    // 모든 api내부 모든클래스 모든메서드에 적용하기
    @Pointcut("execution(* com.example.firstproject.api.*.*(..))")
    private void cut() {
    }

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

    // 28-7. 서버 재실행후 생성후 반환값 로깅 확인
}

