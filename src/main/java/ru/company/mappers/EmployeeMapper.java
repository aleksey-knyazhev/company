package ru.company.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.company.domain.Employee;
import ru.company.dto.EmployeeDto;

@Mapper(componentModel = MappingConstants.ComponentModel.JSR330)
public interface EmployeeMapper {
    EmployeeDto toDto(Employee employee);

    Employee toEntity(EmployeeDto dto);
}
