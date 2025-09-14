package com.reliaquest.api.connector;

import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.dto.EmployeeResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeConnector {
    Flux<EmployeeResponseDto> getAllEmployees();

    Mono<EmployeeResponseDto> getEmployeeById(String id);

    Mono<EmployeeResponseDto> createEmployee(EmployeeDto employeeDto);

    Mono<Void> deleteEmployeeByName(String name);
}
