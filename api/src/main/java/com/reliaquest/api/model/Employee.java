package com.reliaquest.api.model;

import lombok.Data;

@Data
public class Employee {
    private String id;
    private String employeeName;
    private int employeeSalary;
    private int employeeAge;
    private String employeeTitle;
    private String employeeEmail;
}
