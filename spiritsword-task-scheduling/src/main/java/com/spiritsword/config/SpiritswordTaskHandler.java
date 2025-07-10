package com.spiritsword.config;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpiritswordTaskHandler {
    String executorId() default "";
}
