package ru.company;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import ru.company.dto.CompanyDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = "test")
class CompanyControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testCreateCompany() {
        CompanyDto created = createCompany("Create Company");
        assertNotNull(created.getId());
    }

    private CompanyDto createCompany(String name) {
        HttpResponse<CompanyDto> response = client.toBlocking()
                .exchange(HttpRequest.POST("/companies", new CompanyDto(name)), CompanyDto.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        return response.body();
    }

    @Test
    void testCreateAndListCompanies() {
        CompanyDto created = createCompany("Create and list Companies");
        assertNotNull(created.getId());
        assertEquals("Create and list Companies", created.getName());

        List<CompanyDto> companies = client.toBlocking()
                .retrieve(HttpRequest.GET("/companies"), Argument.listOf(CompanyDto.class));

        assertFalse(companies.isEmpty());
        assertTrue(companies.stream().anyMatch(c -> c.getName().equals("Create and list Companies")));
    }

    @Test
    void testGetCompany() {
        CompanyDto created = createCompany("Get Company");
        CompanyDto found = client.toBlocking()
                .retrieve(HttpRequest.GET("/companies/" + created.getId()), CompanyDto.class);

        assertEquals(created.getId(), found.getId());
        assertEquals("Get Company", found.getName());
    }

    @Test
    void testUpdateCompany() {
        CompanyDto created = createCompany("Update Company");
        CompanyDto updateDto = new CompanyDto("Updated Company");
        HttpResponse<CompanyDto> updateResponse = client.toBlocking()
                .exchange(HttpRequest.PUT("/companies/" + created.getId(), updateDto), CompanyDto.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatus());
        assertEquals(created.getId(), updateResponse.body().getId());
        assertEquals("Updated Company", updateResponse.body().getName());
    }

    @Test
    void testDeleteCompany() {
        CompanyDto created = createCompany("Delete Company");
        HttpResponse<?> deleteResponse = client.toBlocking()
                .exchange(HttpRequest.DELETE("/companies/" + created.getId()));

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatus());

        HttpClientResponseException exception = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(HttpRequest.GET("/companies/" + created.getId()), CompanyDto.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
}
