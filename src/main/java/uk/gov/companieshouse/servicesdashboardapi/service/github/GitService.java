package uk.gov.companieshouse.servicesdashboardapi.service.github;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitCustomProperty;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import java.util.List;
import java.util.Map;

@Service
public class GitService {

    @Value("${gh.home}")
    String urlHome;

    @Value("${gh.api}")
    String api;

    @Value("${gh.token.secret}")
    String token;

    @Value("${gh.org}")
    String org;

    @Value("${gh.header.accept}")
    String headerAccept;

    @Value("${gh.releases.perPage}")
    Integer releasesPerPage;

    private final JsonMapper jsonMapper;

    private final RestTemplate restTemplate;

    private HttpEntity<String> httpEntity;

    public GitService(JsonMapper jsonMapper, RestTemplate restTemplate) {
        this.jsonMapper = jsonMapper;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void init() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + token);
        headers.set("Accept", headerAccept);

        this.httpEntity = new HttpEntity<>(headers);
    }

    public GitCustomProperty[] getCustomProperties(String project) {
        String customPropEndpoint = String.format("%s/repos/%s/%s/properties/values", api, org, project);
        try {
            ResponseEntity<GitCustomProperty[]> response = restTemplate.exchange(
                    customPropEndpoint,
                    HttpMethod.GET,
                    httpEntity,
                    GitCustomProperty[].class
            );

            return response.getBody();
        } catch (Exception e) {
            ApiLogger.info("Failed to retrieve Git Custom Properties for " + project + ": " + e.getMessage());
        }
        return new GitCustomProperty[0];
    }

    public String getRepoOwner(GitCustomProperty[] properties) {
        if (properties != null) {
            for (GitCustomProperty property : properties) {
                if ("team-code-owner".equals(property.getPropertyName())) {
                    return property.getValue();
                }
            }
        }
        return "No-Owner";
    }

    public String getServiceArea(GitCustomProperty[] properties) {
        if (properties != null) {
            for (GitCustomProperty property : properties) {
                if ("service-code-owner".equals(property.getPropertyName())) {
                    return property.getValue();
                }
            }
        }
        return "No-Service-Area";
    }

    public GitInfo getRepoInfo(String project) {

        GitInfo gitInfo = new GitInfo();

        // ex https://api.github.com/repos/companieshouse/ch.gov.uk/
        String repoEndpoint = String.format("%s/repos/%s/%s/", api, org, project);

        gitInfo.setRepo(String.format("%s/%s", urlHome, project));

        try {
            // Get the main programming language
            ResponseEntity<String> response = restTemplate.exchange(repoEndpoint + "languages", HttpMethod.GET, httpEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String languagesJson = response.getBody();

                JsonNode languagesNode = jsonMapper.readTree(languagesJson);

                // Find the main language (the one with the most bytes)
                Map<String, Integer> languagesMap = jsonMapper.convertValue(languagesNode, new TypeReference<>() {
                });
                int maxBytes = 0;
                for (Map.Entry<String, Integer> entry : languagesMap.entrySet()) {
                    if (entry.getValue() > maxBytes) {
                        maxBytes = entry.getValue();
                        gitInfo.setLang(entry.getKey());
                    }
                }
            }

            // Get the release info
            String uri = UriComponentsBuilder.fromUriString(repoEndpoint + "releases").queryParam("per_page", releasesPerPage).toUriString();
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    httpEntity,
                    String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<GitReleaseInfo> releases = jsonMapper.readValue(response.getBody(), new TypeReference<>() {
                });
                gitInfo.setReleases(releases);
            }

            GitCustomProperty[] properties = getCustomProperties(project);

            // add repo's owner
            gitInfo.setOwner(getRepoOwner(properties));

            // add repo's service area
            gitInfo.setServiceArea(getServiceArea(properties));

        } catch (Exception e) {
            ApiLogger.info("Failed to retrieve Git info for " + gitInfo.getRepo() + ": " + e.getMessage());
        }

        return gitInfo;
    }

}
