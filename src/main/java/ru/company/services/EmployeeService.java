package ru.company.services;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ru.company.domain.Employee;
import ru.company.repositories.CompanyRepository;
import ru.company.repositories.EmployeeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class EmployeeService {

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    CompanyRepository companyRepository;

    public Optional<Employee> create(Employee employee, UUID companyId) {
        return companyRepository.findById(companyId)
                .map(company -> {
                    employee.setCompany(company);
                    return employeeRepository.save(employee);
                });
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> findById(UUID id) {
        return employeeRepository.findById(id);
    }

    public Optional<Employee> update(UUID id, Employee employeeData, UUID companyId) {
        return companyRepository.findById(companyId)
                .flatMap(company -> employeeRepository.findById(id)
                        .map(employee -> {
                            employee.setName(employeeData.getName());
                            employee.setAge(employeeData.getAge());
                            employee.setCompany(company);
                            return employeeRepository.update(employee);
                        }));
    }

    public boolean delete(UUID id) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employeeRepository.delete(employee);
                    return true;
                })
                .orElse(false);
    }
}
