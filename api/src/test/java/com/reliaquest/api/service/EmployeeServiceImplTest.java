package com.reliaquest.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.reliaquest.api.connector.EmployeeConnector;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.dto.EmployeeResponseDto;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class EmployeeServiceImplTest {

    @Mock
    private EmployeeConnector employeeConnector;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeServiceImpl(employeeConnector);
    }

    @Test
    void getAllEmployees_Success() {
        List<EmployeeResponseDto> employees = Arrays.asList(
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com"),
                new EmployeeResponseDto("2", "Jane Smith", 60000, 35, "Manager", "jane@example.com"));
        when(employeeConnector.getAllEmployees()).thenReturn(Flux.fromIterable(employees));

        StepVerifier.create(employeeService.getAllEmployees())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getEmployeesByNameSearch_Success() {
        List<EmployeeResponseDto> employees = Arrays.asList(
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com"),
                new EmployeeResponseDto("2", "Johnny Smith", 55000, 32, "Developer", "johnny@example.com"));
        when(employeeConnector.getAllEmployees()).thenReturn(Flux.fromIterable(employees));

        StepVerifier.create(employeeService.getEmployeesByNameSearch("John"))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getEmployeeById_Success() {
        EmployeeResponseDto employee =
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com");
        when(employeeConnector.getEmployeeById("1")).thenReturn(Mono.just(employee));

        StepVerifier.create(employeeService.getEmployeeById("1"))
                .expectNext(employee)
                .verifyComplete();
    }

    @Test
    void getEmployeeById_NotFound() {
        when(employeeConnector.getEmployeeById("1")).thenReturn(Mono.error(new EmployeeNotFoundException("1")));

        StepVerifier.create(employeeService.getEmployeeById("1"))
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    void getHighestSalaryOfEmployees_Success() {
        List<EmployeeResponseDto> employees = Arrays.asList(
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com"),
                new EmployeeResponseDto("2", "Jane Smith", 60000, 35, "Manager", "jane@example.com"));
        when(employeeConnector.getAllEmployees()).thenReturn(Flux.fromIterable(employees));

        StepVerifier.create(employeeService.getHighestSalaryOfEmployees())
                .expectNext(60000)
                .verifyComplete();
    }

    @Test
    void getTop10HighestEarningEmployeeNames_Success() {
        List<EmployeeResponseDto> employees = Arrays.asList(
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com"),
                new EmployeeResponseDto("2", "Jane Smith", 60000, 35, "Manager", "jane@example.com"),
                new EmployeeResponseDto("3", "Bob Johnson", 55000, 40, "Developer", "bob@example.com"));
        when(employeeConnector.getAllEmployees()).thenReturn(Flux.fromIterable(employees));

        StepVerifier.create(employeeService.getTop10HighestEarningEmployeeNames())
                .expectNext("Jane Smith", "Bob Johnson", "John Doe")
                .verifyComplete();
    }

    @Test
    void createEmployee_Success() {
        EmployeeDto employeeDto = new EmployeeDto("John Doe", 50000, 30, "Developer");
        EmployeeResponseDto createdEmployee =
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com");
        when(employeeConnector.createEmployee(any(EmployeeDto.class))).thenReturn(Mono.just(createdEmployee));

        StepVerifier.create(employeeService.createEmployee(employeeDto))
                .expectNext(createdEmployee)
                .verifyComplete();
    }

    @Test
    void deleteEmployeeById_Success() {
        EmployeeResponseDto employee =
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com");
        when(employeeConnector.getEmployeeById("1")).thenReturn(Mono.just(employee));
        when(employeeConnector.deleteEmployeeByName("John Doe")).thenReturn(Mono.empty());

        StepVerifier.create(employeeService.deleteEmployeeById("1")).verifyComplete();
    }
}
