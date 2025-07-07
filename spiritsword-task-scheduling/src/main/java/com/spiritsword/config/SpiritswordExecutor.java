package com.spiritsword.config;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
@Component
public @interface SpiritswordExecutor {
    String executorId() default "";
}
