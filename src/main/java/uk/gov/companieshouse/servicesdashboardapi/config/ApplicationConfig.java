package uk.gov.companieshouse.servicesdashboardapi.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.OpenTelemetryServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerRequestObservationConvention;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.ssm.SsmClient;
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

    @Bean
    public SsmClient ssmClient() {
        return SsmClient.create();
    }

    /**
     * Replaces Spring's default {@code ServerRequestObservationConvention}, which tags spans with
     * generic key names (e.g. "method", "status"), with one that follows the stable OpenTelemetry
     * HTTP semantic conventions (e.g. "http.request.method", "http.response.status_code",
     * "http.route"). Without this, exported spans are missing the attributes our OTel tracing
     * dashboards rely on to display the request method and to flag error responses correctly.
     */
    @Bean
    public ServerRequestObservationConvention serverRequestObservationConvention() {
        return new OpenTelemetryServerRequestObservationConvention();
    }

}
