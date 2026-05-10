package ru.company;

import ru.company.dto.CompanyDto;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(environments = "test")
@Testcontainers
class CompanyControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testCreateAndListCompanies() {
        // Создаем DTO
        CompanyDto dto = new CompanyDto();
        dto.setName("Test Company");

        // POST /companies
        var request = HttpRequest.POST("/companies", dto);
        var response = client.toBlocking().exchange(request, CompanyDto.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        CompanyDto saved = response.body();
        assertNotNull(saved.getId());
        assertEquals("Test Company", saved.getName());

        // GET /companies
        List<CompanyDto> companies = client.toBlocking()
                .retrieve(HttpRequest.GET("/companies"), io.micronaut.core.type.Argument.listOf(CompanyDto.class));

        assertFalse(companies.isEmpty());
        assertTrue(companies.stream().anyMatch(c -> c.getName().equals("Test Company")));
    }
}
