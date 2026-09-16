package uk.gov.companieshouse.servicesdashboardapi.service.deptrack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.servicesdashboardapi.config.GenRestTemplate;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.CustomJsonMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(classes = {
        GetAllProjects.class,
        GenRestTemplate.class,
        CustomJsonMapper.class
})
@TestPropertySource(properties = {
        "dt.server.baseurl=http://localhost",
        "dt.server.endpoint.proj=/api/v1/project",
        "dt.server.header.apikey=X-API-Key",
        "dt.server.apikey.secret=test-key",
        "dt.server.header.totcount=x-total-count"
})
class GetAllProjectsIntegrationTest {

    @Autowired
    private GetAllProjects getAllProjects;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

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

        server.expect(requestTo("http://localhost/api/v1/project?offset=0"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-API-Key", "test-key"))
                .andRespond(withSuccess(firstPage, MediaType.APPLICATION_JSON)
                        .header("x-total-count", "2"));

        server.expect(requestTo("http://localhost/api/v1/project?offset=1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-API-Key", "test-key"))
                .andRespond(withSuccess(secondPage, MediaType.APPLICATION_JSON));

        List<DepTrackProjectInfo> result = getAllProjects.fetch();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("project-one", result.get(0).getName());
        assertEquals("project-two", result.get(1).getName());
        assertEquals(5, result.get(1).getMetrics().getCritical());

        server.verify();
    }
}
