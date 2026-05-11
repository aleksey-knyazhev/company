package ru.company.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import ru.company.dto.EmployeeDto;
import ru.company.mappers.EmployeeMapper;
import ru.company.services.EmployeeService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/employees")
public class EmployeeController {

    @Inject
    EmployeeService employeeService;

    @Inject
    EmployeeMapper employeeMapper;

    @Post
    public HttpResponse<EmployeeDto> create(@Body @Valid EmployeeDto dto) {
        return employeeService.create(employeeMapper.toEntity(dto), dto.getCompanyId())
                .map(employee -> HttpResponse.ok(employeeMapper.toDto(employee)))
                .orElseGet(HttpResponse::notFound);
    }

    @Get
    public List<EmployeeDto> getAll() {
        return employeeService.findAll().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Get("/{id}")
    public HttpResponse<EmployeeDto> getById(@PathVariable UUID id) {
        return employeeService.findById(id)
                .map(employee -> HttpResponse.ok(employeeMapper.toDto(employee)))
                .orElseGet(HttpResponse::notFound);
    }

    @Put("/{id}")
    public HttpResponse<EmployeeDto> update(@PathVariable UUID id, @Body @Valid EmployeeDto dto) {
        return employeeService.update(id, employeeMapper.toEntity(dto), dto.getCompanyId())
                .map(employee -> HttpResponse.ok(employeeMapper.toDto(employee)))
                .orElseGet(HttpResponse::notFound);
    }

    @Delete("/{id}")
    public HttpResponse<?> delete(@PathVariable UUID id) {
        if (employeeService.delete(id)) {
            return HttpResponse.noContent();
        }
        return HttpResponse.notFound();
    }
}
