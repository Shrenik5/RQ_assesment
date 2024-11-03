package com.reliaquest.api.service.employee;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.common.Constants;
import com.reliaquest.api.exception.ResponseEntityException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.reliaquest.api.service.communication.CommunicationService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    public static final String NAME = "name";
    @Value("${employee.base.url}")
    private String employeeBaseUrl;

    @Autowired
    CommunicationService communicationService;

    @Autowired
    private ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Value("${employee.module.endpoint}")
    private String employeeModuleEndpoint;

   
    public List<Employee> getAllEmployees() {
        logger.info("inside employee service: getAllEmployees");
        String completeEndpoint = employeeBaseUrl +employeeModuleEndpoint;
        ResponseEntity<JsonNode> responseEntity = communicationService.httpGet(completeEndpoint);
        return processResponse(responseEntity, new TypeReference<>() {});
    }


    public List<Employee> getEmployeesByNameSearch(String searchString) {

        logger.info("inside employee service: getEmployeesByNameSearch : {}", searchString);

        List<Employee> allEmployees = getAllEmployees();

        return allEmployees.stream()
                .filter(emp ->
                        emp.getEmployeeName() != null && emp.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }


    public Employee getEmployeeById(String id) {

        logger.info("inside employee service: getEmployeeById : {}", id);
        String completeEndpoint = employeeBaseUrl +employeeModuleEndpoint + "/"+id;
        ResponseEntity<JsonNode> responseEntity = communicationService.httpGet(completeEndpoint);
        Employee emp = processResponse(responseEntity, new TypeReference<>() {});

        return emp;
    }

    public Integer getHighestSalaryOfEmployees() {
        logger.info("inside employee service: getHighestSalaryOfEmployees");
        return getAllEmployees().stream()
                .map(Employee::getEmployeeSalary)
                .max(Integer::compareTo)
                .orElse(Integer.MIN_VALUE);
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {

        logger.info("inside employee service: getTopTenHighestEarningEmployeeNames");
        List<Employee> listOfEmployees = getAllEmployees();
        List<String> resultedEmployees = listOfEmployees.stream()
                .sorted((o1, o2) -> o2.getEmployeeSalary() - o1.getEmployeeSalary())
                .map(Employee::getEmployeeName).limit(10)
                .collect(Collectors.toList());

        logger.info("resultedEmployees-->>{}",resultedEmployees);

        return resultedEmployees;
    }

   
    public Employee createEmployee(Map<String, Object> employeeInput) {

        logger.info("inside employee service: createEmployee {}",employeeInput);
        String completeEndpoint = employeeBaseUrl +employeeModuleEndpoint;

        ResponseEntity<JsonNode> responseEntity = communicationService.httpPost(completeEndpoint, employeeInput);
        return processResponse(responseEntity, new TypeReference<>() {});
    }


    public Boolean deleteEmployeeById(String id) {

        logger.info("inside employee service: deleteEmployeeById {}",id);
        String completeEndpoint = employeeBaseUrl +employeeModuleEndpoint;

        Employee employee = getEmployeeById(id);
        String empName = employee.getEmployeeName();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(NAME, empName);


        ResponseEntity<JsonNode> responseEntity = communicationService.httpDelete(completeEndpoint,requestBody);

        Boolean status = processResponse(responseEntity, new TypeReference<>() {});
        return status;
    }

    private <T> T processResponse(ResponseEntity<JsonNode> responseEntity, TypeReference<T> type) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            logger.info("API execution was successful, Http Code: {}", responseEntity.getStatusCodeValue());
            Response response = mapper.convertValue(responseEntity.getBody(), Response.class);
            if (Constants.SUCCESS_RESPONSE_STR.equalsIgnoreCase(response.getStatus())) {
                return mapper.convertValue(response.getData(), type);
            } else {
                logger.error("API execution was not successful, status: {}", response.getStatus());
                throw new RuntimeException("Internal Server Error");
            }
        } else {
            logger.error("API execution was failed with Http Code: {}", responseEntity.getStatusCodeValue());
            throw new RuntimeException("Something went wrong !!!");
        }
    }
}
