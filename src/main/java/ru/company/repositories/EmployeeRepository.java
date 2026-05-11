package ru.company.repositories;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import ru.company.domain.Employee;

import java.util.UUID;

@Repository("default")
public interface EmployeeRepository extends CrudRepository<Employee, UUID> {
}
