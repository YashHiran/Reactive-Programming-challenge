package com.reliaquest.api.connector;

import static com.reliaquest.api.config.Constants.EMPLOYEE_BASE_PATH;
import static com.reliaquest.api.config.Constants.EMPLOYEE_BY_ID_PATH;

import com.reliaquest.api.dto.*;
import com.reliaquest.api.exception.EmployeeApiException;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.exception.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeConnectorImpl implements EmployeeConnector {

    private final WebClient webClient;

    @Override
    public Flux<EmployeeResponseDto> getAllEmployees() {
        return webClient
                .get()
                .uri(EMPLOYEE_BASE_PATH)
                .retrieve()
                .bodyToFlux(EmployeesResponseWrapper.class)
                .doOnNext(response -> log.debug("Response status of getAllEmployees: {}", response.getStatus()))
                .flatMap(wrapper -> Flux.fromIterable(wrapper.getData()))
                .onErrorResume(this::handleError);
    }

    @Override
    public Mono<EmployeeResponseDto> getEmployeeById(String id) {
        return webClient
                .get()
                .uri(EMPLOYEE_BY_ID_PATH, id)
                .retrieve()
                .bodyToMono(CreateEmployeeResponseWrapper.class)
                .doOnNext(response -> log.debug("Response status of getEmployeeById: {}", response.getStatus()))
                .map(CreateEmployeeResponseWrapper::getData)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException.NotFound) {
                        return Mono.error(new EmployeeNotFoundException(id));
                    }
                    return handleError(e);
                });
    }

    @Override
    public Mono<EmployeeResponseDto> createEmployee(EmployeeDto employeeDto) {
        return webClient
                .post()
                .uri(EMPLOYEE_BASE_PATH)
                .bodyValue(employeeDto)
                .retrieve()
                .bodyToMono(CreateEmployeeResponseWrapper.class)
                .doOnNext(response -> log.debug("Response status of createEmployee: {}", response.getStatus()))
                .map(CreateEmployeeResponseWrapper::getData)
                .onErrorResume(this::handleError);
    }

    @Override
    public Mono<Void> deleteEmployeeByName(String name) {
        return webClient
                .method(HttpMethod.DELETE)
                .uri(EMPLOYEE_BASE_PATH)
                .bodyValue(new DeleteEmployeeInput(name))
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    if (e instanceof WebClientResponseException.NotFound) {
                        return Mono.error(new EmployeeNotFoundException("Employee not found with name: " + name));
                    }
                    return handleError(e).then();
                });
    }

    private <T> Mono<T> handleError(Throwable error) {
        if (error instanceof WebClientResponseException.TooManyRequests) {
            log.warn("Rate limit exceeded. Suggesting retry after 60 seconds.");
            return Mono.error(new TooManyRequestsException("Rate limit exceeded. Please try again after some time."));
        }
        log.error("Error occurred while calling employee API", error);
        return Mono.error(new EmployeeApiException("Failed to process request", error));
    }
}
