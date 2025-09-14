package com.reliaquest.api.mapper;

import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.dto.EmployeeResponseDto;
import com.reliaquest.api.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeName", source = "name")
    @Mapping(target = "employeeSalary", source = "salary")
    @Mapping(target = "employeeAge", source = "age")
    @Mapping(target = "employeeTitle", source = "title")
    @Mapping(target = "employeeEmail", expression = "java(generateEmail(dto.getName()))")
    Employee dtoToEmployee(EmployeeDto dto);

    @Mapping(target = "name", source = "employeeName")
    @Mapping(target = "salary", source = "employeeSalary")
    @Mapping(target = "age", source = "employeeAge")
    @Mapping(target = "title", source = "employeeTitle")
    EmployeeDto employeeToDto(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", expression = "java(generateEmail(employeeDto.getName()))")
    EmployeeResponseDto dtoToResponse(EmployeeDto employeeDto);

    default String generateEmail(String name) {
        if (name == null) {
            return null;
        }
        String cleanName = name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return cleanName + "@company.com";
    }
}
