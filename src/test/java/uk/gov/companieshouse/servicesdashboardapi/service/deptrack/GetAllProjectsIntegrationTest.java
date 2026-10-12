package uk.gov.companieshouse.servicesdashboardapi.service.deptrack;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import uk.gov.companieshouse.servicesdashboardapi.AbstractIntegrationTest;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("SpringBootApplicationProperties")
@TestPropertySource(properties = {
        "dt.server.endpoint.proj=/api/v1/project",
        "dt.server.header.apikey=X-API-Key",
        "dt.server.header.totcount=x-total-count"
})
class GetAllProjectsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private GetAllProjects getAllProjects;

    @Test
    void shouldFetchProjectsFromDependencyTrack() {
        String firstPage = """
                [
                    {
                        "name": "project-one",
                        "version": "1.0.0",
                        "uuid": "11111111-1111-1111-1111-111111111111",
                        "lastBomImport": 1724366968904,
                        "tags": [{"name": "lang:java"}],
                        "metrics": {
                            "critical": 1,
                            "high": 2,
                            "medium": 3,
                            "low": 4,
                            "vulnerabilities": 10,
                            "components": 20,
                            "policyViolationsTotal": 0,
                            "policyViolationsWarn": 0,
                            "policyViolationsFail": 0
                        }
                    }
                ]
                """;

        String secondPage = """
                [
                    {
                        "name": "project-two",
                        "version": "2.0.0",
                        "uuid": "22222222-2222-2222-2222-222222222222",
                        "lastBomImport": 1724366969900,
                        "tags": [{"name": "lang:java"}],
                        "metrics": {
                            "critical": 5,
                            "high": 6,
                            "medium": 7,
                            "low": 8,
                            "vulnerabilities": 30,
                            "components": 40,
                            "policyViolationsTotal": 3,
                            "policyViolationsWarn": 2,
                            "policyViolationsFail": 1
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
                        .withHeader("x-total-count", "2")
                        .withBody(firstPage)));

        EXTERNAL_APIS.stubFor(get(urlPathEqualTo("/api/v1/project"))
                .withQueryParam("offset", equalTo("1"))
                .withHeader("X-API-Key", equalTo("test-dt-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(secondPage)));

        List<DepTrackProjectInfo> result = getAllProjects.fetch();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("project-one", result.get(0).getName());
        assertEquals("project-two", result.get(1).getName());
        assertEquals(5, result.get(1).getMetrics().getCritical());

    }
}
