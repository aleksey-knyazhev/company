package ru.company.mappers;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import ru.company.domain.Company;
import ru.company.domain.Employee;
import ru.company.dto.EmployeeDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(environments = "test")
class EmployeeMapperTest {

    @Inject
    EmployeeMapper employeeMapper;

    @Test
    void testToDto() {
        UUID companyId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        Company company = Company.builder()
                .id(companyId)
                .name("Employee Mapper Company")
                .build();
        Employee employee = Employee.builder()
                .id(employeeId)
                .name("Mapper Employee")
                .age(30)
                .company(company)
                .build();

        EmployeeDto dto = employeeMapper.toDto(employee);

        assertNotNull(dto);
        assertEquals(employeeId, dto.getId());
        assertEquals("Mapper Employee", dto.getName());
        assertEquals(30, dto.getAge());
        assertEquals(companyId, dto.getCompanyId());
    }

    @Test
    void testToEntity() {
        UUID employeeId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        EmployeeDto dto = new EmployeeDto(employeeId, "Mapper Employee", 31, companyId);

        Employee employee = employeeMapper.toEntity(dto);

        assertNotNull(employee);
        assertEquals(employeeId, employee.getId());
        assertEquals("Mapper Employee", employee.getName());
        assertEquals(31, employee.getAge());
        assertNull(employee.getCompany());
    }
}
