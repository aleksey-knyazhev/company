package ru.company.scheduling;

import io.micronaut.context.annotation.Prototype;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.company.services.CompanyService;
import ru.company.services.EmployeeService;

@Prototype
public class CompanySummaryJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(CompanySummaryJob.class);

    private final CompanyService companyService;
    private final EmployeeService employeeService;

    public CompanySummaryJob(CompanyService companyService, EmployeeService employeeService) {
        this.companyService = companyService;
        this.employeeService = employeeService;
    }

    @Override
    public void execute(JobExecutionContext context) {
        int companiesCount = companyService.findAll().size();
        int employeesCount = employeeService.findAll().size();

        LOG.info("Company summary: companies={}, employees={}", companiesCount, employeesCount);
    }
}
