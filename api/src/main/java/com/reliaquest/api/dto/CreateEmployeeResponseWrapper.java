package com.reliaquest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateEmployeeResponseWrapper {
    private EmployeeResponseDto data;
    private String status;
}
