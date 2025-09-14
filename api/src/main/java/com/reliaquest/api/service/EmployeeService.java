package com.reliaquest.api.service;

import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.dto.EmployeeResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {
    Flux<EmployeeResponseDto> getAllEmployees();

    Flux<EmployeeResponseDto> getEmployeesByNameSearch(String nameFragment);

    Mono<EmployeeResponseDto> getEmployeeById(String id);

    Mono<Integer> getHighestSalaryOfEmployees();

    Flux<String> getTop10HighestEarningEmployeeNames();

    Mono<EmployeeResponseDto> createEmployee(EmployeeDto employeeDto);

    Mono<Void> deleteEmployeeById(String id);
}
