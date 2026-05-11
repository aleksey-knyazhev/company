package ru.company.controllers;

import ru.company.dto.CompanyDto;
import ru.company.mappers.CompanyMapper;
import ru.company.services.CompanyService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/companies")
public class CompanyController {

    @Inject
    CompanyService companyService;

    @Inject
    CompanyMapper companyMapper;

    @Post
    public CompanyDto create(@Body @Valid CompanyDto dto) {
        return companyMapper.toDto(companyService.create(companyMapper.toEntity(dto)));
    }

    @Get
    public List<CompanyDto> getAll() {
        return companyService.findAll().stream()
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Get("/{id}")
    public HttpResponse<CompanyDto> getById(@PathVariable UUID id) {
        return companyService.findById(id)
                .map(company -> HttpResponse.ok(companyMapper.toDto(company)))
                .orElseGet(HttpResponse::notFound);
    }

    @Put("/{id}")
    public HttpResponse<CompanyDto> update(@PathVariable UUID id, @Body @Valid CompanyDto dto) {
        return companyService.update(id, companyMapper.toEntity(dto))
                .map(company -> HttpResponse.ok(companyMapper.toDto(company)))
                .orElseGet(HttpResponse::notFound);
    }

    @Delete("/{id}")
    public HttpResponse<?> delete(@PathVariable UUID id) {
        if (companyService.delete(id)) {
            return HttpResponse.noContent();
        }
        return HttpResponse.notFound();
    }
}
