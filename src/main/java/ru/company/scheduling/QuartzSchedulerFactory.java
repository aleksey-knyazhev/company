package ru.company.scheduling;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

@Factory
@Requires(property = "quartz.enabled", notEquals = "false")
public class QuartzSchedulerFactory {

    @Bean(preDestroy = "shutdown")
    @Singleton
    Scheduler quartzScheduler(JobFactory jobFactory) throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.setJobFactory(jobFactory);
        scheduler.start();
        return scheduler;
    }
}
