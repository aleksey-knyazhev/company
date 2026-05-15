package ru.company.scheduling;

import io.micronaut.context.annotation.Requires;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

@Singleton
@Requires(property = "quartz.enabled", notEquals = "false")
public class QuartzJobsInitializer {

    private static final String JOB_NAME = "company-summary-job";
    private static final String TRIGGER_NAME = "company-summary-trigger";

    private final Scheduler scheduler;

    public QuartzJobsInitializer(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @EventListener
    void onStartup(ServerStartupEvent event) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(CompanySummaryJob.class)
                .withIdentity(JOB_NAME)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(TRIGGER_NAME)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * * * ?"))
                .build();

        scheduler.scheduleJob(job, trigger);
    }
}
