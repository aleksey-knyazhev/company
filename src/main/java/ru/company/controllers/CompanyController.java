package ru.company.controllers;

import ru.company.domain.Company;
import ru.company.dto.CompanyDto;
import ru.company.mappers.CompanyMapper;
import ru.company.repositories.CompanyRepository;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/companies")
public class CompanyController {

    @Inject
    CompanyRepository companyRepository;

    @Inject
    CompanyMapper companyMapper;

    @Post
    public CompanyDto create(@Body @Valid CompanyDto dto) {
        Company saved = companyRepository.save(companyMapper.toEntity(dto));
        return companyMapper.toDto(saved);
    }

    @Get
    public List<CompanyDto> getAll() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
    }
}