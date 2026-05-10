package ru.company.mappers;

import ru.company.domain.Company;
import ru.company.dto.CompanyDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JSR330)
public interface CompanyMapper {

    // Маппинг из сущности БД в объект для API
    CompanyDto toDto(Company company);

    // Маппинг из объекта API в сущность БД
    Company toEntity(CompanyDto dto);
}