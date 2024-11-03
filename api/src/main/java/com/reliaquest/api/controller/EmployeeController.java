package com.reliaquest.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.reliaquest.api.model.Employee;

import java.util.List;
import java.util.Map;

import com.reliaquest.api.service.employee.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController implements IEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    private static final Logger logger = LoggerFactory.getLogger(IEmployeeController.class);

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {

        logger.info("Inside controller --> getAllEmployees");
        List<Employee> allEmployees = employeeService.getAllEmployees();
        return ResponseEntity.ok(allEmployees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        logger.info("Inside controller --> getEmployeesByNameSearch");
        List<Employee> allEmployees = employeeService.getEmployeesByNameSearch(searchString);
        return ResponseEntity.ok(allEmployees);
    }

    @Override
    public ResponseEntity getEmployeeById(String id) {

        logger.info("Inside controller --> getEmployeeById");
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Inside controller --> getHighestSalaryOfEmployees");
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }


    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Inside controller --> getTopTenHighestEarningEmployeeNames");
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Override
    public ResponseEntity createEmployee(Object employeeInput) {
        logger.info("Inside controller --> createEmployee");
        return ResponseEntity.ok(employeeService.createEmployee((Map<String, Object>) employeeInput));
    }

    @Override
    public ResponseEntity deleteEmployeeById(String id) {
        logger.info("Inside controller --> deleteEmployeeById");
        return ResponseEntity.ok(employeeService.deleteEmployeeById(id));
    }


}
