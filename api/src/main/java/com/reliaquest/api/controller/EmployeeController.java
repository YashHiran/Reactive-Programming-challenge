package com.reliaquest.api.controller;

import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.dto.EmployeeResponseDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public Mono<ResponseEntity<?>> getAllEmployees() {
        log.info("Received request to fetch all employees");
        return employeeService
                .getAllEmployees()
                .collectList()
                .map(employees -> {
                    if (employees.isEmpty()) {
                        log.info("No employees found or unable to parse response");
                        return ResponseEntity.noContent().build();
                    }
                    log.info("Retrieved {} employees", employees.size());
                    return ResponseEntity.ok(employees);
                })
                .doOnError(e -> log.error("Error fetching all employees", e));
    }

    @GetMapping("/search/{searchString}")
    public Mono<ResponseEntity<?>> getEmployeesByNameSearch(@PathVariable String searchString) {
        log.info("Searching employees with name fragment: {}", searchString);
        return employeeService
                .getEmployeesByNameSearch(searchString)
                .collectList()
                .map(employees -> {
                    if (employees.isEmpty()) {
                        log.info("No employees found for name fragment: {}", searchString);
                        return ResponseEntity.noContent().build();
                    }
                    log.info("Found {} employees for name fragment: {}", employees.size(), searchString);
                    return ResponseEntity.ok(employees);
                })
                .doOnError(e -> log.error("Error searching employees with name fragment: {}", searchString, e));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<EmployeeResponseDto>> getEmployeeById(@PathVariable String id) {
        log.info("Fetching employee with id: {}", id);
        return employeeService
                .getEmployeeById(id)
                .map(employee -> {
                    log.info("Retrieved employee with id: {}", id);
                    return ResponseEntity.ok(employee);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Employee not found with id: {}", id);
                    return Mono.error(new EmployeeNotFoundException(id));
                }))
                .doOnError(e -> log.error("Error fetching employee with id: {}", id, e));
    }

    @GetMapping("/highest-salary")
    public Mono<ResponseEntity<Integer>> getHighestSalaryOfEmployees() {
        log.info("Fetching highest salary of employees");
        return employeeService
                .getHighestSalaryOfEmployees()
                .map(salary -> {
                    log.info("Highest salary found: {}", salary);
                    return ResponseEntity.ok(salary);
                })
                .defaultIfEmpty(ResponseEntity.noContent().build())
                .doOnError(e -> log.error("Error fetching highest salary", e));
    }

    @GetMapping("/top-ten-earners")
    public Mono<ResponseEntity<?>> getTopTenHighestEarningEmployeeNames() {
        log.info("Fetching top 10 highest earning employee names");
        return employeeService
                .getTop10HighestEarningEmployeeNames()
                .collectList()
                .map(names -> {
                    if (names.isEmpty()) {
                        log.info("No top earners found");
                        return ResponseEntity.noContent().build();
                    }
                    log.info("Retrieved {} top earning employees", names.size());
                    return ResponseEntity.ok(names);
                })
                .doOnError(e -> log.error("Error fetching top earning employees", e));
    }

    @PostMapping
    public Mono<ResponseEntity<EmployeeResponseDto>> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        log.info("Creating new employee: {}", employeeDto);
        return employeeService
                .createEmployee(employeeDto)
                .map(createdEmployee -> {
                    log.info("Employee created successfully: {}", createdEmployee);
                    return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
                })
                .doOnError(e -> log.error("Error creating employee: {}", employeeDto, e));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEmployeeById(@PathVariable String id) {
        log.info("Deleting employee with id: {}", id);
        return employeeService
                .deleteEmployeeById(id)
                .then(Mono.fromCallable(() -> {
                    log.info("Employee deleted successfully with id: {}", id);
                    return ResponseEntity.noContent().<Void>build();
                }))
                .onErrorResume(EmployeeNotFoundException.class, e -> {
                    log.warn("Attempted to delete non-existent employee with id: {}", id);
                    return Mono.just(ResponseEntity.notFound().build());
                })
                .doOnError(e -> log.error("Error deleting employee with id: {}", id, e));
    }
}
