package ru.company.services;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.company.domain.Company;
import ru.company.domain.Employee;
import ru.company.repositories.CompanyRepository;
import ru.company.repositories.EmployeeRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = "test", transactional = false)
class EmployeeServiceTest {

    @Inject
    EmployeeService employeeService;

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
        Company company = createCompany("Employee Service Company");

        Employee created = employeeService.create(Employee.builder()
                .name("Service Employee")
                .age(30)
                .build(), company.getId()).orElseThrow();

        assertNotNull(created.getId());
        assertEquals("Service Employee", employeeService.findById(created.getId()).orElseThrow().getName());
        assertEquals(company.getId(), created.getCompany().getId());
    }

    @Test
    void testCreateWithMissingCompanyReturnsEmpty() {
        assertTrue(employeeService.create(Employee.builder()
                .name("Missing Company Employee")
                .age(31)
                .build(), UUID.randomUUID()).isEmpty());
    }

    @Test
    void testFindAll() {
        Company company = createCompany("List Employee Service Company");
        employeeService.create(Employee.builder().name("First Service Employee").age(32).build(), company.getId());
        employeeService.create(Employee.builder().name("Second Service Employee").age(33).build(), company.getId());

        assertTrue(employeeService.findAll().size() >= 2);
    }

    @Test
    void testUpdate() {
        Company firstCompany = createCompany("First Employee Service Company");
        Company secondCompany = createCompany("Second Employee Service Company");
        Employee created = employeeService.create(Employee.builder()
                .name("Before Update")
                .age(34)
                .build(), firstCompany.getId()).orElseThrow();

        Employee updated = employeeService.update(created.getId(), Employee.builder()
                .name("After Update")
                .age(35)
                .build(), secondCompany.getId()).orElseThrow();

        assertEquals(created.getId(), updated.getId());
        assertEquals("After Update", updated.getName());
        assertEquals(35, updated.getAge());
        assertEquals(secondCompany.getId(), updated.getCompany().getId());
    }

    @Test
    void testDelete() {
        Company company = createCompany("Delete Employee Service Company");
        Employee created = employeeService.create(Employee.builder()
                .name("Delete Service Employee")
                .age(36)
                .build(), company.getId()).orElseThrow();

        boolean deleted = employeeService.delete(created.getId());

        assertTrue(deleted);
        assertFalse(employeeRepository.existsById(created.getId()));
    }

    private Company createCompany(String name) {
        return companyRepository.save(Company.builder()
                .name(name)
                .build());
    }
}
