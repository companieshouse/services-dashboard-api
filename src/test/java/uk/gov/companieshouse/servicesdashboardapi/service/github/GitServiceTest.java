package uk.gov.companieshouse.servicesdashboardapi.service.github;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitCustomProperty;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitServiceTest {

    @Mock
    RestTemplate restTemplate;

    private GitService gitService;

    @BeforeEach
    void setUp() {
        JsonMapper jsonMapper = JsonMapper.builder().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        gitService = new GitService(jsonMapper, restTemplate);
    }

    @Test
    void shouldReturnCustomPropertiesFromApiResponse() {

        ReflectionTestUtils.setField(gitService, "api", "https://api.github.com");
        ReflectionTestUtils.setField(gitService, "org", "companieshouse");
        ReflectionTestUtils.setField(gitService, "httpEntity", new HttpEntity<>((Object) null));

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
        ReflectionTestUtils.setField(gitService, "api", "https://api.github.com");
        ReflectionTestUtils.setField(gitService, "org", "companieshouse");
        ReflectionTestUtils.setField(gitService, "httpEntity", new HttpEntity<>((Object) null));

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
        assertEquals("No-Owner", gitService.getRepoOwner(new GitCustomProperty[0]));
        assertEquals("No-Owner", gitService.getRepoOwner(null));
    }

    @Test
    void shouldReturnNoServiceAreaFromCustomProperties() {
        assertEquals("No-Service-Area", gitService.getServiceArea(new GitCustomProperty[0]));
        assertEquals("No-Service-Area", gitService.getServiceArea(null));
    }

    @Test
    void shouldReturnServiceAreaFromCustomProperties() {
        GitCustomProperty teamOwner = new GitCustomProperty();
        teamOwner.setPropertyName("team-code-owner");
        teamOwner.setValue("team-photon");

        GitCustomProperty serviceArea = new GitCustomProperty();
        serviceArea.setPropertyName("service-code-owner");
        serviceArea.setValue("Common Components");

        String area = gitService.getServiceArea(new GitCustomProperty[]{teamOwner, serviceArea});

        assertEquals("Common Components", area);
    }

    @Test
    void shouldReturnRepoInfoWithDominantLanguageReleasesOwnerAndServiceArea() {
        ReflectionTestUtils.setField(gitService, "urlHome", "https://github.com/companieshouse");
        ReflectionTestUtils.setField(gitService, "api", "https://api.github.com");
        ReflectionTestUtils.setField(gitService, "org", "companieshouse");
        ReflectionTestUtils.setField(gitService, "releasesPerPage", 5);
        ReflectionTestUtils.setField(gitService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(gitService, "httpEntity", new HttpEntity<>((Object) null));

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
        )).thenReturn(ResponseEntity.ok("[{\"tag_name\":\"v1.0.0\",\"published_at\":\"2026-01-01T00:00:00Z\"}]"));

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
        ReflectionTestUtils.setField(gitService, "urlHome", "https://github.com/companieshouse");
        ReflectionTestUtils.setField(gitService, "api", "https://api.github.com");
        ReflectionTestUtils.setField(gitService, "org", "companieshouse");
        ReflectionTestUtils.setField(gitService, "releasesPerPage", 5);
        ReflectionTestUtils.setField(gitService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(gitService, "httpEntity", new HttpEntity<>((Object) null));

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
