package ru.company.services;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import ru.company.domain.Company;
import ru.company.repositories.CompanyRepository;
import ru.company.repositories.EmployeeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class CompanyService {

    @Inject
    CompanyRepository companyRepository;

    @Inject
    EmployeeRepository employeeRepository;

    public Company create(Company company) {
        return companyRepository.save(company);
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Optional<Company> findById(UUID id) {
        return companyRepository.findById(id);
    }

    public Optional<Company> update(UUID id, Company companyData) {
        return companyRepository.findById(id)
                .map(company -> {
                    company.setName(companyData.getName());
                    return companyRepository.update(company);
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        return companyRepository.findById(id)
                .map(company -> {
                    employeeRepository.deleteByCompanyId(company.getId());
                    companyRepository.delete(company);
                    return true;
                })
                .orElse(false);
    }
}
