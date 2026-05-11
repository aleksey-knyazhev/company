package ru.company.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Serdeable
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private UUID id;

    @NotBlank
    private String name;

    @NotNull
    private Integer age;

    @NotNull
    private UUID companyId;
}
