package com.reliaquest.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.employee.EmployeeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * <strong>Important Note</strong>: <br />
 * This Test covers the end-to-end testing
 * <ol>
 *     <li>Employee service is NOT MOCKED rather it is injected</li>
 *     <li>Api service is NOT MOCKED rather it is injected</li>
 *     <li>The only mocked service or class is RestTemplate, which calls the external Rest APIs
 *     from <a href="https://dummy.restapiexample.com/">https://dummy.restapiexample.com</a>
 *     </li>
 * </ol>
 * <p>Which makes it call the API via controller and execute all the service method till it reaches RestTemplate and there only the response is mocked</p>
 *
 */

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@AutoConfigureMockMvc
class RqChallengeApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    RestTemplate restTemplate;

    @Mock
    EmployeeService employeeService;

    @InjectMocks
    private EmployeeController controller;

    private static JsonNode allEmployeeJson;
    private static JsonNode singleEmployeeJson;

    private static JsonNode deleteEmployeeResponse;
    private static List<String> empNames;

    static final String URL_STR ="http://localhost:8112/api/v1/employee";

    @BeforeAll
    public static void init() throws JsonProcessingException {
        allEmployeeJson = com.example.rqchallenge.TestUtils.readJson("allEmp.json");
        assertNotNull(allEmployeeJson);
        singleEmployeeJson = com.example.rqchallenge.TestUtils.readJson("singleEmp.json");
        assertNotNull(singleEmployeeJson);

        ObjectMapper objectMapper = new ObjectMapper();
        deleteEmployeeResponse = objectMapper.readTree( "{ \"data\": true, \"status\": \"Successfully processed request.\" }");

        empNames = Arrays.asList( "Shrenik Shirgave",
                "Perry Parisian",
                "Jenise Okuneva",
                "Man Kohler",
                "Adrianna Hauck DVM",
                "Kellye Kunde",
                "Celena Skiles",
                "Renate Hayes",
                "Kraig Emmerich",
                "Shawna Borer");
    }

    @Test
    void getAllEmployees() throws Exception {
        mockGetAllSuccess();
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    List<Employee> list = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(50, list.size());
                });
    }

    @Test
    void getEmployeesByNameSearch() throws Exception {
        mockSingleEmployee();

        mockMvc.perform(get("/e48b18ff-d2fc-4fab-9ba6-af316495b64e"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    Employee employee = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals("Christal Fahey", employee.getEmployeeName());
                });
    }

    @Test
    void getEmployeeById() throws Exception {
        mockSingleEmployee();
        mockMvc.perform(get("/1"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    Employee emp = mapper.readValue(result.getResponse().getContentAsString(), Employee.class);
                    assertEquals(emp.getEmployeeName(), "Christal Fahey");
                });
    }

    @Test
    void getHighestSalaryOfEmployees() throws Exception {
        mockGetAllSuccess();
        mockMvc.perform(get("/highestSalary"))
                .andExpect(status().isOk())
                .andDo(result -> assertEquals("497881", result.getResponse().getContentAsString()));
    }

    @Test
    void getTopTenHighestEarningEmployeeNames() throws Exception {
        mockGetAllSuccess();
        mockMvc.perform(get("/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    List<String> list = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(10, list.size());
                    assertIterableEquals(empNames, list);
                });
    }

    @Test
    void createEmployee() throws Exception {
        Map<String, Object> inputMap = new LinkedHashMap<>();

        inputMap.put("name","Christal Fahey");
        inputMap.put("salary",305091);
        inputMap.put( "age",34);
        inputMap.put("title","Software Engineer");

        String requestBody = mapper.writeValueAsString(inputMap);
        when(restTemplate.postForEntity(URL_STR, inputMap, JsonNode.class))
                .thenReturn(ResponseEntity.ok(singleEmployeeJson));
        mockMvc.perform(post("/")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    Employee emp = mapper.readValue(result.getResponse().getContentAsString(), Employee.class);
                    assertEquals(emp.getEmployeeName(), "Christal Fahey");

                });
    }

    @Test
    void deleteEmployeeById() throws Exception {
        mockSingleEmployee();
        mockSingleEmployeeDelete();
        doNothing().when(restTemplate).delete("/");

        String responseString = "{\"data\": true, \"status\": \"Successfully processed request.\"}";

        mockMvc.perform(delete("/1"))
                .andExpect(status().isOk())
                .andDo(result -> assertEquals("true", result.getResponse().getContentAsString()));
    }
    private void mockGetAllSuccess() {
        when(restTemplate.getForEntity(URL_STR, JsonNode.class))
                .thenReturn(ResponseEntity.ok(allEmployeeJson));
    }

    private void mockSingleEmployee() {
        when(restTemplate.getForEntity(matches(URL_STR + "/[\\w-]+"), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(singleEmployeeJson));
    }


    private void mockSingleEmployeeDelete() {
        when(restTemplate.exchange(
                matches(URL_STR),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(JsonNode.class)
        )).thenReturn(ResponseEntity.ok(deleteEmployeeResponse));
    }

}