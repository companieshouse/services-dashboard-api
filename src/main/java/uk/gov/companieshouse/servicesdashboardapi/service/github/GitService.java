package uk.gov.companieshouse.servicesdashboardapi.service.github;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import uk.gov.companieshouse.servicesdashboardapi.model.github.GitCustomProperty;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.utils.CustomJsonMapper;

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

   @Autowired
   private CustomJsonMapper jsonMapper;

   @Autowired
   private RestTemplate restTemplate;

   private HttpEntity<String> httpEntity;

    @PostConstruct
    private void init() {
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "token " + token);
      headers.set("Accept", headerAccept);

      this.httpEntity = new HttpEntity<>(headers);
   }

   public String getRepoOwner(String project) {
      String customPropEndpoint = String.format("%s/repos/%s/%s/properties/values", api, org, project);
      try {
         ResponseEntity<GitCustomProperty[]> response = restTemplate.exchange(
            customPropEndpoint,
            HttpMethod.GET,
            httpEntity,
            GitCustomProperty[].class
         );

         GitCustomProperty[] properties = response.getBody();

         // Filter and find the value of "team-code-owner"
         if (properties != null) {
            for (GitCustomProperty property : properties) {
               if ("team-code-owner".equals(property.getPropertyName())) {
                  return property.getValue();
               }
            }
         }
      } catch (Exception e) {
         ApiLogger.info("Failed to retrieve Git Repo Owner " + project + ": " + e.getMessage());
      }
      return "No-Owner";
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

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode languagesNode = objectMapper.readTree(languagesJson);

            // Find the main language (the one with the most bytes)
            int maxBytes = 0;
            Iterator<Map.Entry<String, JsonNode>> fields = languagesNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String languageName = field.getKey();
                int bytes = field.getValue().asInt();

                if (bytes > maxBytes) {
                    maxBytes = bytes;
                    gitInfo.setLang(languageName);
                }
            }
         }

         // Get the releases info
         String uri = UriComponentsBuilder.fromUriString(repoEndpoint + "releases").queryParam("per_page", releasesPerPage).toUriString();
         response = restTemplate.exchange(
            uri,
            HttpMethod.GET,
            httpEntity,
            String.class);
         if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<GitReleaseInfo> releases = jsonMapper.readValue(response.getBody(), new TypeReference<List<GitReleaseInfo>>() {});
            gitInfo.setReleases(releases);
         }

         // add repo's owner
         gitInfo.setOwner(getRepoOwner(project));

      } catch (Exception e) {
         ApiLogger.info("Failed to retrieve Git info for " + gitInfo.getRepo() + ": " + e.getMessage());
      }

      return gitInfo;
   }

}
