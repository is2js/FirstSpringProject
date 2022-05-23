package com.example.firstproject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//28-8.
@Target({ElementType.TYPE, ElementType.METHOD}) // 어노테이션 대상 지정
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 유지기간
public @interface RunningTime {
}
