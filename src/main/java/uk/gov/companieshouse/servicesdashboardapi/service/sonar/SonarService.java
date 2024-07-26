package uk.gov.companieshouse.servicesdashboardapi.service.sonar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.servicesdashboardapi.model.sonar.SonarProjectInfo;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


import uk.gov.companieshouse.servicesdashboardapi.utils.JsonMapper;


@Service
public class SonarService {

    @Value("${sonar.url}")
    private String sonarUrl;

    @Value("${sonar.token}")
    private String sonarToken;

    @Value("${sonar.metrics}")
    private String metrics;

    @Value("${sonar.projkeys.preamble}")
    private String projectKeyPreamble;

    @Value("${sonar.endpoint.measures}")
    private String endpointMeasures;

    private RestTemplate restTemplate;

    @Autowired
    private JsonMapper jsonMapper;

    public SonarService() {
        this.restTemplate = new RestTemplate();
    }

    private String[] getProjKeys(String project) {
      String[] projKeys = new String[2];

      projKeys[0] = projectKeyPreamble + project ; // this should be the standard key
      projKeys[1] = project; // this is a fallback key to try

      return projKeys;
   }

    public void fetchMetrics(String project) {

      String[] projKeys = getProjKeys(project);

      HttpHeaders headers = new HttpHeaders();
      headers.setBasicAuth(sonarToken, "");

      HttpEntity<String> entity = new HttpEntity<>(headers);

      for (String projectKey : projKeys) {
         try {
            String url = String.format("%s/%s?component=%s&metricKeys=%s",
                                       sonarUrl, endpointMeasures, projectKey, metrics);
            ApiLogger.info("----- Sendig SonarQube request: " + url);
            ResponseEntity<String> response = restTemplate.exchange(
               // URLEncoder.encode(url, StandardCharsets.UTF_8.toString()),
               url,
               HttpMethod.GET, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                  SonarProjectInfo result;
                  result = jsonMapper.readValue(response.getBody(), new TypeReference<SonarProjectInfo>() {});

                  System.out.println(result);
                  break; // Stop the loop on the first successful response
            }
         } catch (Exception e) {
             System.err.println("Failed to fetch Sonar metrics for project " + project + ": " + e.getMessage());
        }
      }
   }
}
