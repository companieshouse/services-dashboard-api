package uk.gov.companieshouse.servicesdashboardapi.service.github;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitCustomProperty;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.CustomJsonMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GitServiceTest {

    @Test
    void shouldReturnCustomPropertiesFromApiResponse() {
        GitService gitService = new GitService();
        RestTemplate restTemplate = mock(RestTemplate.class);

        ReflectionTestUtils.setField(gitService, "api", "https://api.github.com");
        ReflectionTestUtils.setField(gitService, "org", "companieshouse");
        ReflectionTestUtils.setField(gitService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(gitService, "httpEntity", new HttpEntity<>(null));

        GitCustomProperty owner = new GitCustomProperty();
        owner.setPropertyName("team-code-owner");
        owner.setValue("team-photon");

        GitCustomProperty serviceArea = new GitCustomProperty();
        serviceArea.setPropertyName("service-code-owner");
        serviceArea.setValue("Common Components");

        GitCustomProperty[] expected = new GitCustomProperty[]{owner, serviceArea};
        String endpoint = "https://api.github.com/repos/companieshouse/my-service/properties/values";

        when(restTemplate.exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GitCustomProperty[].class)
        )).thenReturn(ResponseEntity.ok(expected));

        GitCustomProperty[] result = gitService.getCustomProperties("my-service");

        assertArrayEquals(expected, result);
    }

    @Test
    void shouldReturnEmptyArrayWhenCustomPropertiesRequestFails() {
        GitService gitService = new GitService();
        RestTemplate restTemplate = mock(RestTemplate.class);

        ReflectionTestUtils.setField(gitService, "api", "https://api.github.com");
        ReflectionTestUtils.setField(gitService, "org", "companieshouse");
        ReflectionTestUtils.setField(gitService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(gitService, "httpEntity", new HttpEntity<>(null));

        String endpoint = "https://api.github.com/repos/companieshouse/my-service/properties/values";

        when(restTemplate.exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GitCustomProperty[].class)
        )).thenThrow(new RuntimeException("GitHub API unavailable"));

        GitCustomProperty[] result = gitService.getCustomProperties("my-service");

        assertEquals(0, result.length);
    }

    @Test
    void shouldReturnRepoOwnerFromCustomProperties() {
        GitService gitService = new GitService();

        GitCustomProperty teamOwner = new GitCustomProperty();
        teamOwner.setPropertyName("team-code-owner");
        teamOwner.setValue("team-photon");

        GitCustomProperty otherProperty = new GitCustomProperty();
        otherProperty.setPropertyName("service-code-owner");
        otherProperty.setValue("Common Components");

        String owner = gitService.getRepoOwner(new GitCustomProperty[]{otherProperty, teamOwner});

        assertEquals("team-photon", owner);
    }

    @Test
    void shouldReturnNoOwnerFromCustomProperties() {
        GitService gitService = new GitService();

        assertEquals("No-Owner", gitService.getRepoOwner(new GitCustomProperty[0]));
        assertEquals("No-Owner", gitService.getRepoOwner(null));
    }

    @Test
    void shouldReturnNoServiceAreaFromCustomProperties() {
        GitService gitService = new GitService();

        assertEquals("No-Service-Area", gitService.getServiceArea(new GitCustomProperty[0]));
        assertEquals("No-Service-Area", gitService.getServiceArea(null));
    }

    @Test
    void shouldReturnServiceAreaFromCustomProperties() {
        GitService gitService = new GitService();

        GitCustomProperty teamOwner = new GitCustomProperty();
        teamOwner.setPropertyName("team-code-owner");
        teamOwner.setValue("team-photon");

        GitCustomProperty serviceArea = new GitCustomProperty();
        serviceArea.setPropertyName("service-code-owner");
        serviceArea.setValue("Common Components");

        String area = gitService.getServiceArea(new GitCustomProperty[]{teamOwner, serviceArea});

        assertEquals("Common Components", area);
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnRepoInfoWithDominantLanguageReleasesOwnerAndServiceArea() throws Exception {
        GitService gitService = new GitService();
        RestTemplate restTemplate = mock(RestTemplate.class);
        CustomJsonMapper jsonMapper = mock(CustomJsonMapper.class);

        ReflectionTestUtils.setField(gitService, "urlHome", "https://github.com/companieshouse");
        ReflectionTestUtils.setField(gitService, "api", "https://api.github.com");
        ReflectionTestUtils.setField(gitService, "org", "companieshouse");
        ReflectionTestUtils.setField(gitService, "releasesPerPage", 5);
        ReflectionTestUtils.setField(gitService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(gitService, "jsonMapper", jsonMapper);
        ReflectionTestUtils.setField(gitService, "httpEntity", new HttpEntity<>(null));

        String baseRepoEndpoint = "https://api.github.com/repos/companieshouse/my-service/";
        String releasesEndpoint = "https://api.github.com/repos/companieshouse/my-service/releases?per_page=5";
        String customPropEndpoint = "https://api.github.com/repos/companieshouse/my-service/properties/values";

        when(restTemplate.exchange(
                eq(baseRepoEndpoint + "languages"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok("{\"Java\":1200,\"Python\":500}"));

        when(restTemplate.exchange(
                eq(releasesEndpoint),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok("releases-json"));

        GitReleaseInfo release = new GitReleaseInfo();
        release.setVersion("v1.0.0");
        release.setDate("2026-01-01T00:00:00Z");
        when(jsonMapper.readValue(eq("releases-json"), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                .thenReturn(List.of(release));

        GitCustomProperty owner = new GitCustomProperty();
        owner.setPropertyName("team-code-owner");
        owner.setValue("team-photon");
        GitCustomProperty serviceArea = new GitCustomProperty();
        serviceArea.setPropertyName("service-code-owner");
        serviceArea.setValue("Common Components");

        when(restTemplate.exchange(
                eq(customPropEndpoint),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(GitCustomProperty[].class)
        )).thenReturn(ResponseEntity.ok(new GitCustomProperty[]{owner, serviceArea}));

        GitInfo result = gitService.getRepoInfo("my-service");

        assertEquals("https://github.com/companieshouse/my-service", result.getRepo());
        assertEquals("Java", result.getLang());
        assertEquals("team-photon", result.getOwner());
        assertEquals("Common Components", result.getServiceArea());
        assertEquals(1, result.getReleases().size());
        assertEquals("v1.0.0", result.getReleases().getFirst().getVersion());
    }

    @Test
    void shouldReturnRepoOnlyWhenGitHubRequestFailsInRepoInfo() {
        GitService gitService = new GitService();
        RestTemplate restTemplate = mock(RestTemplate.class);
        CustomJsonMapper jsonMapper = mock(CustomJsonMapper.class);

        ReflectionTestUtils.setField(gitService, "urlHome", "https://github.com/companieshouse");
        ReflectionTestUtils.setField(gitService, "api", "https://api.github.com");
        ReflectionTestUtils.setField(gitService, "org", "companieshouse");
        ReflectionTestUtils.setField(gitService, "releasesPerPage", 5);
        ReflectionTestUtils.setField(gitService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(gitService, "jsonMapper", jsonMapper);
        ReflectionTestUtils.setField(gitService, "httpEntity", new HttpEntity<>(null));

        String baseRepoEndpoint = "https://api.github.com/repos/companieshouse/my-service/";
        when(restTemplate.exchange(
                eq(baseRepoEndpoint + "languages"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(""));

        when(restTemplate.exchange(
                eq("https://api.github.com/repos/companieshouse/my-service/releases?per_page=5"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("GitHub API unavailable"));

        GitInfo result = gitService.getRepoInfo("my-service");

        assertEquals("https://github.com/companieshouse/my-service", result.getRepo());
        assertNull(result.getLang());
        assertNull(result.getReleases());
        assertNull(result.getOwner());
        assertNull(result.getServiceArea());
    }
}
