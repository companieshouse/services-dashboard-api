package uk.gov.companieshouse.servicesdashboardapi.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.DeserializationFeature;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

@Configuration
public class ApplicationConfig {

    @Bean
    public RestTemplate restTemplate() {
        ApiLogger.info("---------Created RestTemplate");
        return new RestTemplate();
    }

    @Bean
    public JsonMapperBuilderCustomizer customizer() {
        return builder -> builder
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

}
