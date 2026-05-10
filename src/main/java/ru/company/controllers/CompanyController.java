package ru.company.controllers;

import ru.company.dto.CompanyDto;
import ru.company.mappers.CompanyMapper;
import ru.company.repositories.CompanyRepository;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller("/companies")
public class CompanyController {

    @Inject
    CompanyRepository companyRepository;

    @Inject
    CompanyMapper companyMapper;

    @Post
    public CompanyDto create(@Body @Valid CompanyDto dto) {
        // Превращаем DTO в Entity
        var entity = companyMapper.toEntity(dto);
        // Сохраняем (UUID сгенерируется автоматически Hibernate-ом)
        var saved = companyRepository.save(entity);
        // Возвращаем сохраненный объект обратно в виде DTO
        return companyMapper.toDto(saved);
    }

    @Get
    public List<CompanyDto> getAll() {
        return StreamSupport.stream(companyRepository.findAll().spliterator(), false)
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
    }
}