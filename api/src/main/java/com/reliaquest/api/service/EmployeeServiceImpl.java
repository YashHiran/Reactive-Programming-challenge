package com.reliaquest.api.service;

import com.reliaquest.api.connector.EmployeeConnector;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.dto.EmployeeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeConnector employeeConnector;

    @Override
    public Flux<EmployeeResponseDto> getAllEmployees() {
        return employeeConnector.getAllEmployees();
    }

    @Override
    public Flux<EmployeeResponseDto> getEmployeesByNameSearch(String nameFragment) {
        return getAllEmployees()
                .filter(employee -> employee.getName().toLowerCase().contains(nameFragment.toLowerCase()));
    }

    @Override
    public Mono<EmployeeResponseDto> getEmployeeById(String id) {
        return employeeConnector.getEmployeeById(id);
    }

    @Override
    public Mono<Integer> getHighestSalaryOfEmployees() {
        return getAllEmployees().map(EmployeeResponseDto::getSalary).reduce(Integer::max);
    }

    @Override
    public Flux<String> getTop10HighestEarningEmployeeNames() {
        return getAllEmployees()
                .sort((e1, e2) -> Integer.compare(e2.getSalary(), e1.getSalary()))
                .map(EmployeeResponseDto::getName)
                .take(10);
    }

    @Override
    public Mono<EmployeeResponseDto> createEmployee(EmployeeDto employeeDto) {
        return employeeConnector.createEmployee(employeeDto);
    }

    @Override
    public Mono<Void> deleteEmployeeById(String id) {
        return employeeConnector.getEmployeeById(id).flatMap(employee -> {
            String name = employee.getName();
            log.info("Deleting employee with name: {}", name);
            return employeeConnector.deleteEmployeeByName(name);
        });
    }
}
