package ru.company;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.company.dto.CompanyDto;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(environments = "test")
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompanyControllerTest implements TestPropertyProvider {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("company_test")
            .withUsername("company")
            .withPassword("company");

    @Inject
    @Client("/")
    HttpClient client;

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }

        return Map.of(
                "datasources.default.url", postgres.getJdbcUrl(),
                "datasources.default.driver-class-name", "org.postgresql.Driver",
                "datasources.default.username", postgres.getUsername(),
                "datasources.default.password", postgres.getPassword(),
                "datasources.default.dialect", "POSTGRES",
                "jpa.default.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"
        );
    }

    @Test
    void testCreateAndListCompanies() {
        CompanyDto dto = new CompanyDto();
        dto.setName("Test Company");

        var request = HttpRequest.POST("/companies", dto);
        var response = client.toBlocking().exchange(request, CompanyDto.class);

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
