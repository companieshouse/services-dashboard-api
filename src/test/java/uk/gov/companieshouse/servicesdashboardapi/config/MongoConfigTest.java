package uk.gov.companieshouse.servicesdashboardapi.config;

import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.gov.companieshouse.servicesdashboardapi.lambda.ConfigSecrets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MongoConfigTest.TestConfig.class})
class MongoConfigTest {

    @Autowired
    private MongoConfig mongoConfig;

    @Autowired
    private MongoProperties mongoProperties;

    /**
     * Tests the `mongoClient` method to ensure a valid `MongoClient` is returned.
     */
    @Test
    void testMongoClient() {
        // Mock property values
        when(mongoProperties.getUser()).thenReturn("testUser");
        when(mongoProperties.getPassword()).thenReturn("testPassword");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("testDb");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");
        // Invoke the method under test
        MongoClient mongoClient = mongoConfig.mongoClient();
        // Assertions
        assertNotNull(mongoClient, "The mongoClient should not be null");
    }

    @Test
    void shouldReturnNonNullMongoDatabaseFactory() {
        when(mongoProperties.getUser()).thenReturn("");
        when(mongoProperties.getPassword()).thenReturn("");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("testDb");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");
        MongoDatabaseFactory factory = mongoConfig.mongoDbFactory();
        assertNotNull(factory, "The MongoDatabaseFactory should not be null");
    }

    @Test
    void shouldCreateDatabaseFactoryWithValidDatabaseName() {
        when(mongoProperties.getUser()).thenReturn("");
        when(mongoProperties.getPassword()).thenReturn("");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("myApplication");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");
        MongoDatabaseFactory factory = mongoConfig.mongoDbFactory();
        assertNotNull(factory, "Factory should be created with valid database name");
    }

    @Test
    void shouldCreateFactoryFromValidMongoClientWithCredentials() {
        when(mongoProperties.getUser()).thenReturn("admin");
        when(mongoProperties.getPassword()).thenReturn("secret");
        when(mongoProperties.getHostandport()).thenReturn("mongodb.example.com:27017");
        when(mongoProperties.getDbname()).thenReturn("production");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");
        MongoDatabaseFactory factory = mongoConfig.mongoDbFactory();
        assertNotNull(factory, "Factory should be created with authenticated MongoClient");
    }

    @Test
    void shouldReturnMongoTemplateWhenConfigurationIsValid() {
        when(mongoProperties.getUser()).thenReturn("");
        when(mongoProperties.getPassword()).thenReturn("");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("servicesDashboard");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");

        MongoTemplate mongoTemplate = mongoConfig.mongoTemplate();

        assertNotNull(mongoTemplate, "MongoTemplate should be created for valid configuration");
    }

    @Test
    void shouldReturnMongoTemplateWhenCredentialsAreProvided() {
        when(mongoProperties.getUser()).thenReturn("testUser");
        when(mongoProperties.getPassword()).thenReturn("testPassword");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("servicesDashboard");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");

        MongoTemplate mongoTemplate = mongoConfig.mongoTemplate();

        assertNotNull(mongoTemplate, "MongoTemplate should be created when credentials are set");
    }

    @Test
    void shouldThrowExceptionWhenDatabaseNameIsEmptyForMongoTemplate() {
        when(mongoProperties.getUser()).thenReturn("");
        when(mongoProperties.getPassword()).thenReturn("");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");

        assertThrows(IllegalArgumentException.class, () -> mongoConfig.mongoTemplate());
    }

    @Test
    void shouldThrowExceptionWhenProtocolIsInvalidForMongoTemplate() {
        when(mongoProperties.getUser()).thenReturn("");
        when(mongoProperties.getPassword()).thenReturn("");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("servicesDashboard");
        when(mongoProperties.getProtocol()).thenReturn("not-a-valid-protocol");

        assertThrows(IllegalArgumentException.class, () -> mongoConfig.mongoTemplate());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public ConfigSecrets configSecrets() {
            return mock(ConfigSecrets.class);
        }

        @Bean
        public MongoConfig mongoConfig() {
            return new MongoConfig();
        }

        @Bean
        public MongoProperties mongoProperties() {
            return mock(MongoProperties.class);
        }
    }
}
