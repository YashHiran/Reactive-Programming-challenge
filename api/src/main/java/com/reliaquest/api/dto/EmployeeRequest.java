package com.reliaquest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeRequest {
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
}
