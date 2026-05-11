package ru.company.controllers;

import ru.company.domain.Company;
import ru.company.dto.CompanyDto;
import ru.company.mappers.CompanyMapper;
import ru.company.repositories.CompanyRepository;
import ru.company.repositories.EmployeeRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/companies")
public class CompanyController {

    @Inject
    CompanyRepository companyRepository;

    @Inject
    CompanyMapper companyMapper;

    @Inject
    EmployeeRepository employeeRepository;

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

    @Get("/{id}")
    public HttpResponse<CompanyDto> getById(@PathVariable UUID id) {
        return companyRepository.findById(id)
                .map(company -> HttpResponse.ok(companyMapper.toDto(company)))
                .orElseGet(HttpResponse::notFound);
    }

    @Put("/{id}")
    public HttpResponse<CompanyDto> update(@PathVariable UUID id, @Body @Valid CompanyDto dto) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setName(dto.getName());
                    Company updated = companyRepository.update(company);
                    return HttpResponse.ok(companyMapper.toDto(updated));
                })
                .orElseGet(HttpResponse::notFound);
    }

    @Delete("/{id}")
    @Transactional
    public HttpResponse<?> delete(@PathVariable UUID id) {
        return companyRepository.findById(id)
                .map(company -> {
                    employeeRepository.deleteByCompanyId(company.getId());
                    companyRepository.delete(company);
                    return HttpResponse.noContent();
                })
                .orElseGet(HttpResponse::notFound);
    }
}
