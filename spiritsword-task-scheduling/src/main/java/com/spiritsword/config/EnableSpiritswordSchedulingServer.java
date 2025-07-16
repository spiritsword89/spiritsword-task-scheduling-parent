package com.spiritsword.config;

import com.spiritsword.scheduler.SchedulingServerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(SchedulingServerConfiguration.class)
public @interface EnableSpiritswordSchedulingServer {
}
