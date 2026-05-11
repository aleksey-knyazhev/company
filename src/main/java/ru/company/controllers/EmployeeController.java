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
import ru.company.domain.Company;
import ru.company.domain.Employee;
import ru.company.dto.EmployeeDto;
import ru.company.mappers.EmployeeMapper;
import ru.company.repositories.CompanyRepository;
import ru.company.repositories.EmployeeRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/employees")
public class EmployeeController {

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    CompanyRepository companyRepository;

    @Inject
    EmployeeMapper employeeMapper;

    @Post
    public HttpResponse<EmployeeDto> create(@Body @Valid EmployeeDto dto) {
        return companyRepository.findById(dto.getCompanyId())
                .map(company -> {
                    Employee employee = employeeMapper.toEntity(dto);
                    employee.setCompany(company);
                    Employee saved = employeeRepository.save(employee);
                    return HttpResponse.ok(employeeMapper.toDto(saved));
                })
                .orElseGet(HttpResponse::notFound);
    }

    @Get
    public List<EmployeeDto> getAll() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Get("/{id}")
    public HttpResponse<EmployeeDto> getById(@PathVariable UUID id) {
        return employeeRepository.findById(id)
                .map(employee -> HttpResponse.ok(employeeMapper.toDto(employee)))
                .orElseGet(HttpResponse::notFound);
    }

    @Put("/{id}")
    public HttpResponse<EmployeeDto> update(@PathVariable UUID id, @Body @Valid EmployeeDto dto) {
        Company company = companyRepository.findById(dto.getCompanyId()).orElse(null);
        if (company == null) {
            return HttpResponse.notFound();
        }

        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(dto.getName());
                    employee.setAge(dto.getAge());
                    employee.setCompany(company);
                    Employee updated = employeeRepository.update(employee);
                    return HttpResponse.ok(employeeMapper.toDto(updated));
                })
                .orElseGet(HttpResponse::notFound);
    }

    @Delete("/{id}")
    public HttpResponse<?> delete(@PathVariable UUID id) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employeeRepository.delete(employee);
                    return HttpResponse.noContent();
                })
                .orElseGet(HttpResponse::notFound);
    }
}
