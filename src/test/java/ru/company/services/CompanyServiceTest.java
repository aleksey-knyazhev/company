package ru.company.services;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.company.domain.Company;
import ru.company.domain.Employee;
import ru.company.repositories.CompanyRepository;
import ru.company.repositories.EmployeeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = "test", transactional = false)
class CompanyServiceTest {

    @Inject
    CompanyService companyService;

    @Inject
    CompanyRepository companyRepository;

    @Inject
    EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    void testCreateAndFindById() {
        Company created = companyService.create(Company.builder()
                .name("Service Company")
                .build());

        assertNotNull(created.getId());
        assertEquals("Service Company", companyService.findById(created.getId()).orElseThrow().getName());
    }

    @Test
    void testFindAll() {
        companyService.create(Company.builder().name("First Service Company").build());
        companyService.create(Company.builder().name("Second Service Company").build());

        assertTrue(companyService.findAll().size() >= 2);
    }

    @Test
    void testUpdate() {
        Company created = companyService.create(Company.builder()
                .name("Before Update")
                .build());

        Company updated = companyService.update(created.getId(), Company.builder()
                .name("After Update")
                .build()).orElseThrow();

        assertEquals(created.getId(), updated.getId());
        assertEquals("After Update", updated.getName());
    }

    @Test
    void testDeleteDeletesEmployees() {
        Company company = companyService.create(Company.builder()
                .name("Delete Service Company")
                .build());
        Employee employee = employeeRepository.save(Employee.builder()
                .name("Service Employee")
                .age(30)
                .company(company)
                .build());

        boolean deleted = companyService.delete(company.getId());

        assertTrue(deleted);
        assertFalse(companyRepository.existsById(company.getId()));
        assertFalse(employeeRepository.existsById(employee.getId()));
    }
}
