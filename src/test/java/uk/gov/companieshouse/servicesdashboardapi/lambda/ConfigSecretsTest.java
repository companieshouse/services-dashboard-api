package uk.gov.companieshouse.servicesdashboardapi.lambda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfigSecretsTest {

    private ConfigurableListableBeanFactory beanFactoryMock;
    private MutablePropertySources propertySourcesMock;
    private SsmClient ssmClientMock;
    private ConfigSecrets configSecrets;

    @BeforeEach
    void setUp() {
        beanFactoryMock = mock(ConfigurableListableBeanFactory.class);
        ConfigurableEnvironment environmentMock = mock(ConfigurableEnvironment.class);
        propertySourcesMock = new MutablePropertySources();
        ssmClientMock = mock(SsmClient.class);

        when(beanFactoryMock.getBean(ConfigurableEnvironment.class)).thenReturn(environmentMock);
        when(environmentMock.getPropertySources()).thenReturn(propertySourcesMock);

        configSecrets = new ConfigSecrets();
        ReflectionTestUtils.setField(configSecrets, "ssmClient", ssmClientMock);
        ReflectionTestUtils.setField(configSecrets, "ssmPrefix", "/test/prefix");
    }


    @Test
    void testPostProcessBeanFactory_missingSsmPrefix_throwsException() {
        ReflectionTestUtils.setField(configSecrets, "ssmPrefix", null);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> configSecrets.postProcessBeanFactory(beanFactoryMock)
        );

        assertEquals("Environment variable 'SSM_PREFIX' is not defined", exception.getMessage());
    }

    @Test
    void testPostProcessBeanFactory_withSsmPrefixAndLambda_updatesSecrets() {
        configSecrets.setLambdaFunctionNameOverride("testLambdaFunction");

        Properties initialProperties = new Properties();
        initialProperties.setProperty("db.password.secret", "placeholder");

        PropertiesPropertySource propertiesSource = new PropertiesPropertySource("application", initialProperties);
        propertySourcesMock.addLast(propertiesSource);

        when(ssmClientMock.getParameter(any(GetParameterRequest.class)))
                .thenAnswer(invocation -> {
                    GetParameterRequest request = invocation.getArgument(0);
                    if ("/test/prefix/db_password".equals(request.name())) {
                        return GetParameterResponse.builder()
                                .parameter(Parameter.builder()
                                        .value("decryptedSecret")
                                        .build())
                                .build();
                    }
                    throw new IllegalArgumentException("Unexpected parameter name: " + request.name());
                });

        configSecrets.postProcessBeanFactory(beanFactoryMock);

        ArgumentCaptor<GetParameterRequest> requestCaptor = ArgumentCaptor.forClass(GetParameterRequest.class);
        verify(ssmClientMock).getParameter(requestCaptor.capture());
        assertEquals("/test/prefix/db_password", requestCaptor.getValue().name());
        assertTrue(requestCaptor.getValue().withDecryption());

        PropertySource<?> captured = propertySourcesMock.get("EnvSecrets");
        assertNotNull(captured);

        assertEquals("EnvSecrets", captured.getName());
        Properties props = (Properties) captured.getSource();
        assertEquals("decryptedSecret", props.getProperty("db.password.secret"));
    }

    @Test
    void testPostProcessBeanFactory_propertiesWithoutSecretSuffixRemainUnchanged() {
        configSecrets.setLambdaFunctionNameOverride("testLambdaFunction");

        Properties initialProperties = new Properties();
        initialProperties.setProperty("db.username", "user123");
        initialProperties.setProperty("db.password.secret", "placeholder");

        PropertiesPropertySource propertiesSource = new PropertiesPropertySource("application", initialProperties);
        propertySourcesMock.addLast(propertiesSource);

        when(ssmClientMock.getParameter(any(GetParameterRequest.class)))
                .thenReturn(GetParameterResponse.builder()
                        .parameter(Parameter.builder()
                                .value("decryptedSecret")
                                .build())
                        .build());

        configSecrets.postProcessBeanFactory(beanFactoryMock);

        PropertySource<?> captured = propertySourcesMock.get("EnvSecrets");
        assertNotNull(captured);

        assertEquals("EnvSecrets", captured.getName());
        Properties props = (Properties) captured.getSource();
        assertEquals("user123", props.getProperty("db.username"));
        assertEquals("decryptedSecret", props.getProperty("db.password.secret"));
    }

    @Test
    void testPostProcessBeanFactory_whenSsmClientThrows_setsSecretToEmptyString() {
        configSecrets.setLambdaFunctionNameOverride("testLambdaFunction");

        Properties initialProperties = new Properties();
        initialProperties.setProperty("db.password.secret", "placeholder");

        PropertiesPropertySource propertiesSource = new PropertiesPropertySource("application", initialProperties);
        propertySourcesMock.addLast(propertiesSource);

        when(ssmClientMock.getParameter(any(GetParameterRequest.class)))
                .thenThrow(new RuntimeException("ssm unavailable"));

        configSecrets.postProcessBeanFactory(beanFactoryMock);

        PropertySource<?> captured = propertySourcesMock.get("EnvSecrets");
        assertNotNull(captured);
        Properties props = (Properties) captured.getSource();
        assertEquals("", props.getProperty("db.password.secret"));
    }

    @Test
    void testPostProcessBeanFactory_whenSsmResponseMissingParameter_setsSecretToEmptyString() {
        configSecrets.setLambdaFunctionNameOverride("testLambdaFunction");

        Properties initialProperties = new Properties();
        initialProperties.setProperty("db.password.secret", "placeholder");

        PropertiesPropertySource propertiesSource = new PropertiesPropertySource("application", initialProperties);
        propertySourcesMock.addLast(propertiesSource);

        when(ssmClientMock.getParameter(any(GetParameterRequest.class)))
                .thenReturn(GetParameterResponse.builder().build());

        configSecrets.postProcessBeanFactory(beanFactoryMock);

        PropertySource<?> captured = propertySourcesMock.get("EnvSecrets");
        assertNotNull(captured);
        Properties props = (Properties) captured.getSource();
        assertEquals("", props.getProperty("db.password.secret"));
    }
}
