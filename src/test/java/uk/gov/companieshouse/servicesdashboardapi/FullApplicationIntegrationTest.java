package uk.gov.companieshouse.servicesdashboardapi;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boots the real, unmocked Spring application - the actual {@link ServicesDashboardApiApplication}
 * main configuration, with its normal component scanning - behind a real embedded servlet
 * container, backed by a real MongoDB instance (Testcontainers).
 * <p>
 * The only substitutes here are for the third-party network boundaries this service talks to over
 * HTTP (Dependency-Track, GitHub, SonarQube and endoflife.date): those are served by a real, running
 * WireMock HTTP server rather than being hit live, which would be unreliable/unsafe to depend on in
 * CI. No Spring bean, controller, service, repository or mapper is mocked or stubbed anywhere in this
 * test: every request travels through the genuine REST controller, service, mapper and repository
 * layers, and is persisted to (and re-read from) a real MongoDB.
 * <p>
 * Copilot Review Comment:
 * Booting this class still performs live DNS lookups for four production hosts in ServicesDashboardApiApplication's constructor
 * (ServicesDashboardApiApplication.java:21-24). WireMock cannot intercept those lookups, so an isolated or slow-DNS CI
 * environment can delay this supposedly self-contained integration suite. Please make host-resolution logging
 * disableable/injectable for tests or otherwise prevent those lookups before the application context starts.
 * <p>
 * Maybe worth revisiting this at a later date to refactor and avoid the DNS lookups.
 */


class FullApplicationIntegrationTest extends AbstractIntegrationTest {

    @Test
    void healthcheckRespondsOkOnTheRealRunningApplication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/services-dashboard/healthcheck", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Services Dashboard API Service is healthy");
    }

    @Test
    void listServicesRunsTheFullPipelineAndPersistsToRealMongo() {
        stubDependencyTrack();
        stubGitHub();
        stubSonar();

        ResponseEntity<ProjectInfo[]> response =
                restTemplate.getForEntity("/services-dashboard/list-services", ProjectInfo[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);

        ProjectInfo project = response.getBody()[0];
        assertThat(project.getName()).isEqualTo("test-service");
        assertThat(project.getSonarKey()).isEqualTo("uk.gov.companieshouse:test-service");
        assertThat(project.getSonarMetrics()).containsEntry("bugs", 2);
        assertThat(project.getGitInfo().getLang()).isEqualTo("Java");
        assertThat(project.getGitInfo().getOwner()).isEqualTo("team-x");
        assertThat(project.getGitInfo().getServiceArea()).isEqualTo("service-y");

        // Confirm the whole pipeline (controller -> service -> mapper -> repository) actually
        // persisted the enriched data in the real database, not just returned it over HTTP.
        Query query = new Query(Criteria.where("name").is("test-service"));
        MongoProjectInfo persisted = mongoTemplate.findOne(query, MongoProjectInfo.class, "projects");

        assertThat(persisted).isNotNull();
        assertThat(persisted.getVersions()).hasSize(1);
        assertThat(persisted.getVersions().getFirst().getVersion()).isEqualTo("1.2.3");
        assertThat(persisted.getVersions().getFirst().getUuid()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(persisted.getSonarKey()).isEqualTo("uk.gov.companieshouse:test-service");
        assertThat(persisted.getGitInfo().getOwner()).isEqualTo("team-x");
    }

    @Test
    void endolEndpointFetchesRealLookingDataAndPersistsSingletonConfig() {
        stubEndOfLife();

        ResponseEntity<String> response = restTemplate.getForEntity("/services-dashboard/endol", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Endol ok");

        MongoConfigInfo persisted = mongoTemplate.findById("singletonConfig", MongoConfigInfo.class, "config");

        assertThat(persisted).isNotNull();
        assertThat(persisted.getEndol()).containsKeys(
                "go", "nodejs", "amazon-corretto", "oracle-jdk", "spring-framework", "spring-boot");
        assertThat(persisted.getEndol().get("go")).hasSize(1);
        assertThat(persisted.getEndol().get("go").getFirst().getCycle()).isEqualTo("1.99");
        assertThat(persisted.getLastScan()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
    }

    private void stubDependencyTrack() {
        String body = """
                [
                    {
                        "name": "test-service",
                        "version": "1.2.3",
                        "uuid": "11111111-1111-1111-1111-111111111111",
                        "lastBomImport": 1724366968904,
                        "tags": [
                            {"name": "lang:java"},
                            {"name": "runtime:21.0.1 java-21-amazon-corretto.x86_64"}
                        ],
                        "metrics": {
                            "critical": 1, "high": 2, "medium": 3, "low": 4,
                            "vulnerabilities": 10, "components": 20,
                            "policyViolationsTotal": 0, "policyViolationsWarn": 0, "policyViolationsFail": 0
                        }
                    }
                ]
                """;

        EXTERNAL_APIS.stubFor(get(urlPathEqualTo("/api/v1/project"))
                .withQueryParam("offset", equalTo("0"))
                .withHeader("X-API-Key", equalTo("test-dt-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("x-total-count", "1")
                        .withBody(body)));
    }

    private void stubGitHub() {
        EXTERNAL_APIS.stubFor(get(urlPathEqualTo("/repos/companieshouse/test-service/languages"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"Java\": 5000, \"Kotlin\": 200}")));

        EXTERNAL_APIS.stubFor(get(urlPathEqualTo("/repos/companieshouse/test-service/releases"))
                .withQueryParam("per_page", equalTo("7"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {"tag_name": "1.2.3", "published_at": "2024-05-01T00:00:00Z"},
                                    {"tag_name": "1.2.2", "published_at": "2024-04-01T00:00:00Z"}
                                ]
                                """)));

        EXTERNAL_APIS.stubFor(get(urlPathEqualTo("/repos/companieshouse/test-service/properties/values"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {"property_name": "team-code-owner", "value": "team-x"},
                                    {"property_name": "service-code-owner", "value": "service-y"}
                                ]
                                """)));
    }

    private void stubSonar() {
        EXTERNAL_APIS.stubFor(get(urlPathEqualTo("/api/measures/component"))
                .withQueryParam("component", equalTo("uk.gov.companieshouse:test-service"))
                .withQueryParam("metricKeys", equalTo(
                        "vulnerabilities,bugs,code_smells,coverage,new_vulnerabilities,new_bugs,new_code_smells,new_coverage"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "component": {
                                        "key": "uk.gov.companieshouse:test-service",
                                        "name": "test-service",
                                        "measures": [
                                            {"metric": "bugs", "value": "2", "bestValue": false},
                                            {"metric": "vulnerabilities", "value": "0", "bestValue": true},
                                            {"metric": "coverage", "value": "88.5", "bestValue": false}
                                        ]
                                    }
                                }
                                """)));
    }

    private void stubEndOfLife() {
        for (String project : new String[]{"go", "nodejs", "amazon-corretto", "oracle-jdk", "spring-framework", "spring-boot"}) {
            EXTERNAL_APIS.stubFor(get(urlPathEqualTo("/" + project + ".json"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("""
                                    [
                                        {"cycle": "1.99", "releaseDate": "2024-01-01", "eol": "2025-01-01", "latest": "1.99.1", "latestReleaseDate": "2024-01-02"}
                                    ]
                                    """)));
        }
    }

}
