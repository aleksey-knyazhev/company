package ru.company;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import ru.company.dto.CompanyDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = "test")
class CompanyControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testCreateAndListCompanies() {
        CompanyDto dto = new CompanyDto("Test Company");

        MutableHttpRequest<CompanyDto> request = HttpRequest.POST("/companies", dto);
        HttpResponse<CompanyDto> response = client.toBlocking().exchange(request, CompanyDto.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        CompanyDto saved = response.body();
        assertNotNull(saved.getId());
        assertEquals("Test Company", saved.getName());

        List<CompanyDto> companies = client.toBlocking()
                .retrieve(HttpRequest.GET("/companies"), Argument.listOf(CompanyDto.class));

        assertFalse(companies.isEmpty());
        assertTrue(companies.stream().anyMatch(c -> c.getName().equals("Test Company")));
    }
}
