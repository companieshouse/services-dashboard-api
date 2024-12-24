package uk.gov.companieshouse.servicesdashboardapi.lambda;

import java.util.Properties;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.NonNull;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;



// Config init handler that modifies properties in "application.properties" with custom values
// for properties ending with ".secret" and which are retrieved from AWS Paramet Store.
// This handler is executed before the application context is loaded, and so all the @Value
// annotations are resolved with the custom values.
// This handler is only used when running as a Lambda function (env var "AWS_LAMBDA_FUNCTION_NAME" defined)

@Component
public class ConfigSecrets implements BeanFactoryPostProcessor {

    private final SsmClient ssmClient = SsmClient.create();

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) {

        ApiLogger.info("Loading secrets from AWS Param Store");
        ConfigurableEnvironment environment = beanFactory.getBean(ConfigurableEnvironment.class);

        String lambdaFunctionName = System.getenv("AWS_LAMBDA_FUNCTION_NAME");
        if (lambdaFunctionName != null && !lambdaFunctionName.isEmpty()) {
            ApiLogger.info("Running as Lambda function: " + lambdaFunctionName);
            MutablePropertySources propertySources = environment.getPropertySources();

            // Access properties from application.properties
            Properties properties = new Properties();
            propertySources.forEach(propertySource -> {
                if (propertySource instanceof PropertiesPropertySource) {
                    properties.putAll(((PropertiesPropertySource) propertySource).getSource());
                }
            });

            // Set, from Param. Store, the properties ending with ".secret"
            properties.forEach((key, value) -> {
                String keyStr = key.toString();
                if (keyStr.endsWith(".secret")) {
                    ApiLogger.info("reading SSM param (key: " + keyStr + " value: " + getSecret(keyStr));
                    properties.setProperty(keyStr, getSecret(keyStr));
                }
            });

            // Add modified properties back to the environment (at 1st position, so before application.properties)
            propertySources.addFirst(new PropertiesPropertySource("EnvSecrets", properties));
        }
    }

    private String getSecret(String secretName) {
        try {
            // Fetch the secret value
            GetParameterRequest request = GetParameterRequest.builder()
                .name(secretName)
                .withDecryption(true)
                .build();

            GetParameterResponse response = ssmClient.getParameter(request);
            return response.parameter().value();

        } catch (Exception e) {
            ApiLogger.info("Error fetching secret: " + secretName + " - " + e.getMessage());
            return "";
        }
    }

}
