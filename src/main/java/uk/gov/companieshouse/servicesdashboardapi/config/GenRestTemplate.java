package uk.gov.companieshouse.servicesdashboardapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

@Configuration
public class GenRestTemplate {

    @Bean
    public RestTemplate restTemplate() {
        ApiLogger.info("---------Created RestTemplate");
        return new RestTemplate();
    }
}
