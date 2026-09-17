package uk.gov.companieshouse.servicesdashboardapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check controller for Services Dashboard API
 * <p>
 * TODO - Replace with Spring Boot Actuator health check endpoint in future
 */
@RestController
public class HealthCheckController {

    @GetMapping("/services-dashboard/healthcheck")
    public ResponseEntity<String> healthcheck() {
        return new ResponseEntity<>("Services Dashboard API Service is healthy", HttpStatus.OK);
    }
}
