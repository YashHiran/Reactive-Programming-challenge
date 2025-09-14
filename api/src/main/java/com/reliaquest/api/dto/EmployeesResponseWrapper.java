package com.reliaquest.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeesResponseWrapper {
    private List<EmployeeResponseDto> data;
    private String status;
}
