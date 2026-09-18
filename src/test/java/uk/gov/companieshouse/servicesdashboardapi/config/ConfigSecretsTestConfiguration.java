package uk.gov.companieshouse.servicesdashboardapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.servicesdashboardapi.lambda.ConfigSecrets;

@TestConfiguration
public class ConfigSecretsTestConfiguration {

    /**
     * A real (unmocked) {@link ConfigSecrets} instance. Its bean-factory post-processing only
     * ever does anything when running inside AWS Lambda (env var AWS_LAMBDA_FUNCTION_NAME set),
     * which is never the case here, so this override exists solely to satisfy its constructor
     * guard that otherwise requires the SSM_PREFIX OS environment variable - always supplied by
     * Terraform/Vault in real deployments, but understandably absent on a local test JVM.
     */
    @Bean
    @Primary
    ConfigSecrets configSecrets() {
        ConfigSecrets configSecrets = new ConfigSecrets();
        ReflectionTestUtils.setField(configSecrets, "ssmPrefix", "/test/services-dashboard-api");
        return configSecrets;
    }


}
