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
class CompanyRepositoryTest {

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
        Company saved = companyRepository.save(Company.builder()
                .name("Repository Company")
                .build());

        assertNotNull(saved.getId());

        Company found = companyRepository.findById(saved.getId()).orElseThrow();

        assertEquals(saved.getId(), found.getId());
        assertEquals("Repository Company", found.getName());
    }

    @Test
    void testUpdate() {
        Company saved = companyRepository.save(Company.builder()
                .name("Before Update")
                .build());

        saved.setName("After Update");
        Company updated = companyRepository.update(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("After Update", updated.getName());
    }

    @Test
    void testDelete() {
        Company saved = companyRepository.save(Company.builder()
                .name("Delete Repository Company")
                .build());

        companyRepository.delete(saved);

        assertFalse(companyRepository.existsById(saved.getId()));
    }

    @Test
    void testDeleteWithEmployee() {
        Company saved = companyRepository.save(Company.builder()
                .name("Delete Repository Company")
                .build());
        Employee employee = employeeRepository.save(Employee.builder()
                .name("Delete Repository Employee")
                .age(30)
                .company(saved)
                .build());

        companyRepository.delete(saved);

        assertFalse(companyRepository.existsById(saved.getId()));
        assertFalse(employeeRepository.existsById(employee.getId()));
    }

    @Test
    void testFindAll() {
        companyRepository.save(Company.builder().name("First Repository Company").build());
        companyRepository.save(Company.builder().name("Second Repository Company").build());

        assertTrue(companyRepository.findAll().size() >= 2);
    }
}
