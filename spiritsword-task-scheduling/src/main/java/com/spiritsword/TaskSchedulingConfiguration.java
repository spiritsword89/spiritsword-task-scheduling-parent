package com.spiritsword;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "com.spiritsword", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.spiritsword\\.scheduler")
})
public class TaskSchedulingConfiguration {
}
