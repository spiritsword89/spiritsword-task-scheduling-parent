package com.spiritsword.config;

import com.spiritsword.TaskSchedulingConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(TaskSchedulingConfiguration.class)
public @interface EnableSpiritswordTaskScheduling {
}
