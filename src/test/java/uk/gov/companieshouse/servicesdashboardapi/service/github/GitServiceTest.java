package uk.gov.companieshouse.servicesdashboardapi.service.github;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.servicesdashboardapi.model.github.GitCustomProperty;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
