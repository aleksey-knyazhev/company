package ru.company.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;


@Data
@Serdeable
public class CompanyDto {
    private UUID id;

    @NotBlank
    private String name;

    public CompanyDto(String name) {
        this.name = name;
    }
}
