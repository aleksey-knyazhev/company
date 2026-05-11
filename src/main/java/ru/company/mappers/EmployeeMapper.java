package ru.company.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.company.domain.Employee;
import ru.company.dto.EmployeeDto;

@Mapper(componentModel = MappingConstants.ComponentModel.JSR330)
public interface EmployeeMapper {
    @Mapping(source = "company.id", target = "companyId")
    EmployeeDto toDto(Employee employee);

    @Mapping(target = "company", ignore = true)
    Employee toEntity(EmployeeDto dto);
}
