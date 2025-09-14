package com.reliaquest.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.dto.EmployeeResponseDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEmployees_Success() {
        List<EmployeeResponseDto> employees = Arrays.asList(
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com"),
                new EmployeeResponseDto("2", "Jane Smith", 60000, 35, "Manager", "jane@example.com"));
        when(employeeService.getAllEmployees()).thenReturn(Flux.fromIterable(employees));

        StepVerifier.create(employeeController.getAllEmployees())
                .expectNext(ResponseEntity.ok(employees))
                .verifyComplete();
    }

    @Test
    void getEmployeesByNameSearch_Success() {
        List<EmployeeResponseDto> employees = Arrays.asList(
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com"),
                new EmployeeResponseDto("2", "Johnny Smith", 55000, 32, "Developer", "johnny@example.com"));
        when(employeeService.getEmployeesByNameSearch("John")).thenReturn(Flux.fromIterable(employees));

        StepVerifier.create(employeeController.getEmployeesByNameSearch("John"))
                .expectNext(ResponseEntity.ok(employees))
                .verifyComplete();
    }

    @Test
    void getEmployeeById_Success() {
        EmployeeResponseDto employee =
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com");
        when(employeeService.getEmployeeById("1")).thenReturn(Mono.just(employee));

        StepVerifier.create(employeeController.getEmployeeById("1"))
                .expectNext(ResponseEntity.ok(employee))
                .verifyComplete();
    }

    @Test
    void getEmployeeById_NotFound() {
        when(employeeService.getEmployeeById("1")).thenReturn(Mono.error(new EmployeeNotFoundException("1")));

        StepVerifier.create(employeeController.getEmployeeById("1"))
                .expectErrorMatches(throwable -> throwable instanceof EmployeeNotFoundException
                        && throwable.getMessage().equals("Employee not found with id: 1"))
                .verify();
    }

    @Test
    void getHighestSalaryOfEmployees_Success() {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(Mono.just(60000));

        StepVerifier.create(employeeController.getHighestSalaryOfEmployees())
                .expectNext(ResponseEntity.ok(60000))
                .verifyComplete();
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_Success() {
        List<String> topEarners = Arrays.asList("Jane Smith", "John Doe", "Bob Johnson");
        when(employeeService.getTop10HighestEarningEmployeeNames()).thenReturn(Flux.fromIterable(topEarners));

        StepVerifier.create(employeeController.getTopTenHighestEarningEmployeeNames())
                .expectNext(ResponseEntity.ok(topEarners))
                .verifyComplete();
    }

    @Test
    void createEmployee_Success() {
        EmployeeDto employeeDto = new EmployeeDto("John Doe", 50000, 30, "Developer");
        EmployeeResponseDto createdEmployee =
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com");
        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(Mono.just(createdEmployee));

        StepVerifier.create(employeeController.createEmployee(employeeDto))
                .expectNext(ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee))
                .verifyComplete();
    }

    @Test
    void deleteEmployeeById_Success() {
        when(employeeService.deleteEmployeeById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(employeeController.deleteEmployeeById("1"))
                .expectNext(ResponseEntity.noContent().build())
                .verifyComplete();
    }

    @Test
    void deleteEmployeeById_NotFound() {
        when(employeeService.deleteEmployeeById(anyString()))
                .thenReturn(Mono.error(new EmployeeNotFoundException("1")));

        StepVerifier.create(employeeController.deleteEmployeeById("1"))
                .expectNext(ResponseEntity.notFound().build())
                .verifyComplete();
    }
}
