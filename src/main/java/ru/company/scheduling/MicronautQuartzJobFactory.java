package ru.company.scheduling;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

@Singleton
@Requires(property = "quartz.enabled", notEquals = "false")
public class MicronautQuartzJobFactory implements JobFactory {

    private final BeanContext beanContext;

    public MicronautQuartzJobFactory(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        return beanContext.createBean(bundle.getJobDetail().getJobClass());
    }
}
