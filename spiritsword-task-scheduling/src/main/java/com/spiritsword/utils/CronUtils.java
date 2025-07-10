package com.spiritsword.utils;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

public class CronUtils {
    public static LocalDateTime getNextTriggerTime(String cronExpression) {
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING);
        CronParser parser = new CronParser(cronDefinition);
        Cron cron = parser.parse(cronExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime now = ZonedDateTime.now();
        Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(now);

        if (nextExecution.isPresent()) {
            return nextExecution.get().toLocalDateTime();
        } else {
            throw new IllegalStateException("Cannot find nextExecution time for " + cronExpression);
        }
    }
}
