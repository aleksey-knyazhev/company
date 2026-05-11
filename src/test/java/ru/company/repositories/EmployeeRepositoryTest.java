package ru.company.repositories;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.company.domain.Company;
import ru.company.domain.Employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = "test", transactional = false)
class EmployeeRepositoryTest {

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
    void testSaveAndFindById() {
        Company company = createCompany("Employee Repository Company");
        Employee saved = employeeRepository.save(Employee.builder()
                .name("Repository Employee")
                .age(30)
                .company(company)
                .build());

        assertNotNull(saved.getId());

        Employee found = employeeRepository.findById(saved.getId()).orElseThrow();

        assertEquals(saved.getId(), found.getId());
        assertEquals("Repository Employee", found.getName());
        assertEquals(30, found.getAge());
        assertEquals(company.getId(), found.getCompany().getId());
    }

    @Test
    void testUpdate() {
        Company firstCompany = createCompany("First Repository Company");
        Company secondCompany = createCompany("Second Repository Company");
        Employee saved = employeeRepository.save(Employee.builder()
                .name("Before Update")
                .age(31)
                .company(firstCompany)
                .build());

        saved.setName("After Update");
        saved.setAge(32);
        saved.setCompany(secondCompany);
        Employee updated = employeeRepository.update(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("After Update", updated.getName());
        assertEquals(32, updated.getAge());
        assertEquals(secondCompany.getId(), updated.getCompany().getId());
    }

    @Test
    void testDeleteByCompanyId() {
        Company company = createCompany("Delete Employees Company");
        Employee firstEmployee = employeeRepository.save(Employee.builder()
                .name("First Employee")
                .age(33)
                .company(company)
                .build());
        Employee secondEmployee = employeeRepository.save(Employee.builder()
                .name("Second Employee")
                .age(34)
                .company(company)
                .build());

        employeeRepository.deleteByCompanyId(company.getId());

        assertFalse(employeeRepository.existsById(firstEmployee.getId()));
        assertFalse(employeeRepository.existsById(secondEmployee.getId()));
    }

    @Test
    void testFindAll() {
        Company company = createCompany("List Employees Company");
        employeeRepository.save(Employee.builder().name("First Employee").age(35).company(company).build());
        employeeRepository.save(Employee.builder().name("Second Employee").age(36).company(company).build());

        assertTrue(employeeRepository.findAll().size() >= 2);
    }

    private Company createCompany(String name) {
        return companyRepository.save(Company.builder()
                .name(name)
                .build());
    }
}
