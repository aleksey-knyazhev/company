package ru.company.repositories;

import ru.company.domain.Company;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.UUID;

@Repository("default")
public interface CompanyRepository extends CrudRepository<Company, UUID> {
}