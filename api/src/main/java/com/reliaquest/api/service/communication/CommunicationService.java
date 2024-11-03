package com.reliaquest.api.service.communication;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;

@Service
public class CommunicationService {
    @Autowired
    private RestTemplate restTemplate;


    private static final Logger logger = LoggerFactory.getLogger(CommunicationService.class);

    public ResponseEntity<JsonNode> httpGet(String completeUrl) {
        logger.info("HTTP GET call for: {}", completeUrl);
        return restTemplate.getForEntity(completeUrl, JsonNode.class);
    }


    public ResponseEntity<JsonNode> httpPost(String completeUrl, Map<String, Object> request) {
        logger.info("HTTP POST call for: {}", completeUrl);
        return restTemplate.postForEntity(completeUrl, request, JsonNode.class);
    }

    public ResponseEntity<JsonNode> httpDelete(String completeUrl, Map<String, Object> request) {
        logger.info("HTTP DELETE call for: {} with request body: {}", completeUrl, request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(completeUrl, HttpMethod.DELETE, entity, JsonNode.class);
    }

}
