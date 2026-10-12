package uk.gov.companieshouse.servicesdashboardapi.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongoConfigTest {

    @InjectMocks
    private MongoConfig mongoConfig;

    @Mock
    private MongoProperties mongoProperties;

    private MockedStatic<MongoClients> mongoClients;
    private MongoClient mongoClient;

    @BeforeEach
    void setUp() {
        mongoClient = mock(MongoClient.class);
        mongoClients = mockStatic(MongoClients.class);
        mongoClients.when(() -> MongoClients.create(org.mockito.ArgumentMatchers.any(com.mongodb.MongoClientSettings.class)))
                .thenReturn(mongoClient);
    }

    @AfterEach
    void tearDown() {
        mongoClients.close();
    }

    @Test
    void testMongoClient() {
        when(mongoProperties.getUser()).thenReturn("testUser");
        when(mongoProperties.getPassword()).thenReturn("testPassword");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("testDb");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");

        try (MongoClient client = mongoConfig.mongoClient()) {
            assertNotNull(client, "The mongoClient should not be null");
        }
    }

    @Test
    void shouldReturnNonNullMongoDatabaseFactory() {
        when(mongoProperties.getUser()).thenReturn("");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("testDb");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");

        try (MongoClient client = mongoConfig.mongoClient()) {
            MongoDatabaseFactory factory = mongoConfig.mongoDbFactory(client);
            assertNotNull(factory, "The MongoDatabaseFactory should not be null");
        }
    }

    @Test
    void shouldCreateDatabaseFactoryWithValidDatabaseName() {
        when(mongoProperties.getUser()).thenReturn("");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("myApplication");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");

        try (MongoClient client = mongoConfig.mongoClient()) {
            MongoDatabaseFactory factory = mongoConfig.mongoDbFactory(client);
            assertNotNull(factory, "Factory should be created with valid database name");
        }
    }

    @Test
    void shouldCreateFactoryFromValidMongoClientWithCredentials() {
        when(mongoProperties.getUser()).thenReturn("admin");
        when(mongoProperties.getPassword()).thenReturn("secret");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("production");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");

        try (MongoClient client = mongoConfig.mongoClient()) {
            MongoDatabaseFactory factory = mongoConfig.mongoDbFactory(client);
            assertNotNull(factory, "Factory should be created with authenticated MongoClient");
        }
    }

    @Test
    void shouldReturnMongoTemplateWhenConfigurationIsValid() {
        when(mongoProperties.getUser()).thenReturn("");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("servicesDashboard");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");

        try (MongoClient client = mongoConfig.mongoClient()) {
            MongoDatabaseFactory factory = mongoConfig.mongoDbFactory(client);
            MongoTemplate mongoTemplate = mongoConfig.mongoTemplate(factory);

            assertNotNull(mongoTemplate, "MongoTemplate should be created for valid configuration");
        }
    }

    @Test
    void shouldReturnMongoTemplateWhenCredentialsAreProvided() {
        when(mongoProperties.getUser()).thenReturn("testUser");
        when(mongoProperties.getPassword()).thenReturn("testPassword");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("servicesDashboard");
        when(mongoProperties.getProtocol()).thenReturn("mongodb");

        try (MongoClient client = mongoConfig.mongoClient()) {
            MongoDatabaseFactory factory = mongoConfig.mongoDbFactory(client);
            MongoTemplate mongoTemplate = mongoConfig.mongoTemplate(factory);

            assertNotNull(mongoTemplate, "MongoTemplate should be created when credentials are set");
        }
    }

    @Test
    void shouldThrowExceptionWhenDatabaseNameIsEmptyForMongoTemplate() {
        when(mongoProperties.getDbname()).thenReturn("");

        assertThrows(IllegalArgumentException.class, () -> mongoConfig.mongoDbFactory(mongoClient));
    }

    @Test
    void shouldThrowExceptionWhenProtocolIsInvalidForMongoTemplate() {
        when(mongoProperties.getUser()).thenReturn("");
        when(mongoProperties.getHostandport()).thenReturn("localhost:27017");
        when(mongoProperties.getDbname()).thenReturn("servicesDashboard");
        when(mongoProperties.getProtocol()).thenReturn("not-a-valid-protocol");

        assertThrows(IllegalArgumentException.class, () -> mongoConfig.mongoClient());
    }
}
