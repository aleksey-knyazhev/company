package ru.company;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import ru.company.dto.CompanyDto;
import ru.company.dto.EmployeeDto;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = "test")
class EmployeeControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testCreateAndListEmployees() {
        CompanyDto company = createCompany("Employee Company");
        EmployeeDto created = createEmployee("Ivan Ivanov", 30, company.getId());

        assertNotNull(created.getId());
        assertEquals("Ivan Ivanov", created.getName());
        assertEquals(30, created.getAge());
        assertEquals(company.getId(), created.getCompanyId());

        List<EmployeeDto> employees = client.toBlocking()
                .retrieve(HttpRequest.GET("/employees"), Argument.listOf(EmployeeDto.class));

        assertFalse(employees.isEmpty());
        assertTrue(employees.stream().anyMatch(employee -> employee.getId().equals(created.getId())));
    }

    @Test
    void testGetEmployee() {
        CompanyDto company = createCompany("Get Employee Company");
        EmployeeDto created = createEmployee("Get Employee", 31, company.getId());

        EmployeeDto found = client.toBlocking()
                .retrieve(HttpRequest.GET("/employees/" + created.getId()), EmployeeDto.class);

        assertEquals(created.getId(), found.getId());
        assertEquals("Get Employee", found.getName());
        assertEquals(31, found.getAge());
        assertEquals(company.getId(), found.getCompanyId());
    }

    @Test
    void testUpdateEmployee() {
        CompanyDto firstCompany = createCompany("First Employee Company");
        CompanyDto secondCompany = createCompany("Second Employee Company");
        EmployeeDto created = createEmployee("Before Update", 32, firstCompany.getId());

        EmployeeDto updateDto = new EmployeeDto(null, "After Update", 33, secondCompany.getId());
        HttpResponse<EmployeeDto> updateResponse = client.toBlocking()
                .exchange(HttpRequest.PUT("/employees/" + created.getId(), updateDto), EmployeeDto.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatus());
        assertEquals(created.getId(), updateResponse.body().getId());
        assertEquals("After Update", updateResponse.body().getName());
        assertEquals(33, updateResponse.body().getAge());
        assertEquals(secondCompany.getId(), updateResponse.body().getCompanyId());
    }

    @Test
    void testDeleteEmployee() {
        CompanyDto company = createCompany("Delete Employee Company");
        EmployeeDto created = createEmployee("Delete Employee", 34, company.getId());

        HttpResponse<?> deleteResponse = client.toBlocking()
                .exchange(HttpRequest.DELETE("/employees/" + created.getId()));

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatus());

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.GET("/employees/" + created.getId()), EmployeeDto.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testCreateEmployeeWithMissingCompanyReturnsNotFound() {
        EmployeeDto employeeDto = new EmployeeDto(null, "Missing Company", 35, UUID.randomUUID());

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.POST("/employees", employeeDto), EmployeeDto.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    private CompanyDto createCompany(String name) {
        HttpResponse<CompanyDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/companies", new CompanyDto(name)), CompanyDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        return response.body();
    }

    private EmployeeDto createEmployee(String name, Integer age, UUID companyId) {
        EmployeeDto employeeDto = new EmployeeDto(null, name, age, companyId);
        HttpResponse<EmployeeDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/employees", employeeDto), EmployeeDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        return response.body();
    }
}
