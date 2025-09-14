package com.reliaquest.api.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeResponseWrapper;
import com.reliaquest.api.dto.EmployeeDto;
import com.reliaquest.api.dto.EmployeeResponseDto;
import com.reliaquest.api.dto.EmployeesResponseWrapper;
import com.reliaquest.api.exception.EmployeeApiException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.TooManyRequestsException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

class EmployeeConnectorImplTest {

    private static MockWebServer mockWebServer;
    private EmployeeConnectorImpl employeeConnector;
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        WebClient webClient = WebClient.create(baseUrl);
        employeeConnector = new EmployeeConnectorImpl(webClient);
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllEmployees_Success() throws Exception {
        List<EmployeeResponseDto> employees = Arrays.asList(
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com"),
                new EmployeeResponseDto("2", "Jane Smith", 60000, 35, "Manager", "jane@example.com"));
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(new EmployeesResponseWrapper(employees, "success")))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(employeeConnector.getAllEmployees())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getAllEmployees_TooManyRequests() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(429));

        StepVerifier.create(employeeConnector.getAllEmployees())
                .expectError(TooManyRequestsException.class)
                .verify();
    }

    @Test
    void getEmployeeById_Success() throws Exception {
        EmployeeResponseDto employee =
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com");
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(new CreateEmployeeResponseWrapper(employee, "success")))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(employeeConnector.getEmployeeById("1"))
                .expectNext(employee)
                .verifyComplete();
    }

    @Test
    void getEmployeeById_NotFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(employeeConnector.getEmployeeById("1"))
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }

    @Test
    void createEmployee_Success() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto("John Doe", 50000, 30, "Developer");
        EmployeeResponseDto createdEmployee =
                new EmployeeResponseDto("1", "John Doe", 50000, 30, "Developer", "john@example.com");
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(new CreateEmployeeResponseWrapper(createdEmployee, "success")))
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(employeeConnector.createEmployee(employeeDto))
                .expectNext(createdEmployee)
                .verifyComplete();
    }

    @Test
    void createEmployee_Error() {
        EmployeeDto employeeDto = new EmployeeDto("John Doe", 50000, 30, "Developer");
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(employeeConnector.createEmployee(employeeDto))
                .expectError(EmployeeApiException.class)
                .verify();
    }

    @Test
    void deleteEmployeeByName_Success() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        StepVerifier.create(employeeConnector.deleteEmployeeByName("John Doe")).verifyComplete();
    }

    @Test
    void deleteEmployeeByName_NotFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(employeeConnector.deleteEmployeeByName("John Doe"))
                .expectError(EmployeeNotFoundException.class)
                .verify();
    }
}
