package uk.gov.companieshouse.servicesdashboardapi.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.companieshouse.servicesdashboardapi.config.MongoConfig;
import uk.gov.companieshouse.servicesdashboardapi.config.MongoProperties;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoGitInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoVersionInfo;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"SpringBootApplicationProperties", "SameParameterValue"})
@Testcontainers
@SpringBootTest(classes = {
        MongoRepositoriesIntegrationTest.MongoTestConfiguration.class,
        MongoConfig.class,
        CustomMongoProjectInfoRepositoryImpl.class,
        CustomMongoConfigRepositoryImpl.class
})
@TestPropertySource(properties = {
        "mongo.collectionNameProj=projects",
        "mongo.collectionNameConf=config",
        "mongo.configObjectId=singletonConfig"
})
class MongoRepositoriesIntegrationTest {

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:7.0");

    @Autowired
    private CustomMongoProjectInfoRepository projectRepository;

    @Autowired
    private CustomMongoConfigRepository configRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("MONGODB_PROTOCOL", () -> "mongodb");
        registry.add("MONGODB_USER", () -> "");
        registry.add("MONGODB_PASSWORD", () -> "");
        registry.add("MONGODB_HOST_AND_PORT",
                () -> MONGO.getHost() + ":" + MONGO.getFirstMappedPort());
        registry.add("MONGODB_DBNAME", () -> "services-dashboard-test");
        registry.add("mongo.collectionNameProj", () -> "projects");
        registry.add("mongo.collectionNameConf", () -> "config");
        registry.add("mongo.collection-name-proj", () -> "projects");
        registry.add("mongo.collection-name-conf", () -> "config");
        registry.add("mongo.configObjectId", () -> "singletonConfig");
    }

    private static MongoProjectInfo project(String name, List<MongoVersionInfo> versions,
                                            String sonarKey, String owner) {
        MongoProjectInfo project = new MongoProjectInfo();
        project.setName(name);
        project.setVersions(versions);
        project.setSonarKey(sonarKey);
        project.setSonarMetrics(Map.of("bugs", 1));
        MongoGitInfo gitInfo = new MongoGitInfo();
        gitInfo.setOwner(owner);
        project.setGitInfo(gitInfo);
        return project;
    }

    private static MongoVersionInfo version(String version, String uuid) {
        MongoVersionInfo result = new MongoVersionInfo();
        result.setVersion(version);
        result.setUuid(uuid);
        return result;
    }

    @BeforeEach
    void cleanDatabase() {
        mongoTemplate.getDb().drop();
    }

    @Test
    void savesFindsAndMergesProjectVersionsInMongo() {
        projectRepository.saveProjectInfos(List.of(project("service-a",
                List.of(version("1.0.0", "uuid-1")), "sonar-a", "owner-a")));

        projectRepository.saveProjectInfos(List.of(project("service-a",
                List.of(version("1.0.0", "uuid-1-new"), version("2.0.0", "uuid-2")),
                "sonar-b", "owner-b")));

        MongoProjectInfo saved = projectRepository.findByName("service-a").orElseThrow();

        assertThat(saved.getVersions())
                .extracting(MongoVersionInfo::getVersion)
                .containsExactlyInAnyOrder("1.0.0", "2.0.0");
        assertThat(saved.getVersions()).filteredOn(v -> v.getVersion().equals("1.0.0"))
                .extracting(MongoVersionInfo::getUuid).containsExactly("uuid-1-new");
        assertThat(saved.getSonarKey()).isEqualTo("sonar-b");
        assertThat(saved.getGitInfo().getOwner()).isEqualTo("owner-b");
        assertThat(projectRepository.existsByUuid("service-a", "uuid-2")).isTrue();
        assertThat(projectRepository.existsByUuid("service-a", "unknown")).isFalse();
    }

    @Test
    void upsertsSingletonConfigAndUpdatesItOnSubsequentScan() {
        MongoConfigInfo first = new MongoConfigInfo();
        first.setEndol(Map.of("nodejs", List.of()));
        configRepository.saveConfigInfo(first);

        MongoConfigInfo second = new MongoConfigInfo();
        second.setEndol(Map.of("go", List.of()));
        configRepository.saveConfigInfo(second);

        MongoConfigInfo saved = mongoTemplate
                .findById("singletonConfig", MongoConfigInfo.class, "config");
        assertThat(saved).isNotNull();
        assertThat(saved.getEndol()).containsOnlyKeys("go");
        assertThat(saved.getLastScan()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
    }

    @TestConfiguration
    static class MongoTestConfiguration {
        @Bean
        MongoProperties mongoProperties() {
            MongoProperties properties = new MongoProperties();
            properties.setProtocol("mongodb");
            properties.setUser("");
            properties.setPassword("");
            properties.setHostandport(MONGO.getHost() + ":" + MONGO.getFirstMappedPort());
            properties.setDbname("services-dashboard-test");
            properties.setCollectionNameProj("projects");
            properties.setCollectionNameConf("config");
            return properties;
        }
    }
}
