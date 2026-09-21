package uk.gov.companieshouse.servicesdashboardapi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import uk.gov.companieshouse.servicesdashboardapi.config.ConfigSecretsTestConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ConfigSecretsTestConfiguration.class)
@Testcontainers
@AutoConfigureTestRestTemplate
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public abstract class AbstractIntegrationTest {

    protected static final WireMockServer EXTERNAL_APIS = new WireMockServer(WireMockConfiguration.options().dynamicPort());

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:7.0");

    private static final String AWS_REGION_PROPERTY = "aws.region";

    private static final String PREVIOUS_AWS_REGION = System.getProperty(AWS_REGION_PROPERTY);

    static {
        // Only needed so the real (unmocked) ConfigSecrets bean can build a real SsmClient at
        // startup; that client is never actually invoked outside of AWS Lambda (see below).
        System.setProperty(AWS_REGION_PROPERTY, "eu-west-2");
        startExternalApis();
        Runtime.getRuntime().addShutdownHook(new Thread(AbstractIntegrationTest::stopExternalApis));
    }

    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    protected MongoTemplate mongoTemplate;

    @DynamicPropertySource
    static void applicationProperties(DynamicPropertyRegistry registry) {
        startExternalApis();
        String wireMockUrl = "http://localhost:" + EXTERNAL_APIS.port();

        // Real Mongo, provided by Testcontainers
        registry.add("mongo.protocol.secret", () -> "mongodb");
        registry.add("mongo.user.secret", () -> "");
        registry.add("mongo.password.secret", () -> "");
        registry.add("mongo.hostandport.secret", () -> MONGO.getHost() + ":" + MONGO.getFirstMappedPort());
        registry.add("mongo.dbname.secret", () -> "services-dashboard-full-it");
        registry.add("mongo.collectionNameProj", () -> "projects");
        registry.add("mongo.collectionNameConf", () -> "config");
        registry.add("mongo.configObjectId", () -> "singletonConfig");

        // Third-party HTTP APIs, all served by the same WireMock instance
        registry.add("dt.server.baseurl", () -> wireMockUrl);
        registry.add("dt.server.apikey.secret", () -> "test-dt-key");
        registry.add("gh.api", () -> wireMockUrl);
        registry.add("gh.token.secret", () -> "test-gh-token");
        registry.add("sonar.url", () -> wireMockUrl);
        registry.add("sonar.token.secret", () -> "test-sonar-token");
        registry.add("endol.api.url", () -> wireMockUrl);

        registry.add("deepScan.enabled", () -> "true");
    }

    private static synchronized void startExternalApis() {
        if (!EXTERNAL_APIS.isRunning()) {
            EXTERNAL_APIS.start();
        }
    }

    private static synchronized void stopExternalApis() {
        if (EXTERNAL_APIS.isRunning()) {
            EXTERNAL_APIS.stop();
        }
        if (PREVIOUS_AWS_REGION != null) {
            System.setProperty(AWS_REGION_PROPERTY, PREVIOUS_AWS_REGION);
        } else {
            System.clearProperty(AWS_REGION_PROPERTY);
        }
    }

    @BeforeEach
    void resetState() {
        startExternalApis();
        mongoTemplate.getDb().drop();
        EXTERNAL_APIS.resetAll();
    }

}
