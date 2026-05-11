package ru.company.mappers;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import ru.company.domain.Company;
import ru.company.dto.CompanyDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest(environments = "test")
class CompanyMapperTest {

    @Inject
    CompanyMapper companyMapper;

    @Test
    void testToDto() {
        UUID id = UUID.randomUUID();
        Company company = Company.builder()
                .id(id)
                .name("Mapper Company")
                .build();

        CompanyDto dto = companyMapper.toDto(company);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Mapper Company", dto.getName());
    }

    @Test
    void testToEntity() {
        CompanyDto dto = new CompanyDto("Mapper Company");
        UUID id = UUID.randomUUID();
        dto.setId(id);

        Company company = companyMapper.toEntity(dto);

        assertNotNull(company);
        assertEquals(id, company.getId());
        assertEquals("Mapper Company", company.getName());
    }
}
