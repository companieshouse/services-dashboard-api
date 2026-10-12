package uk.gov.companieshouse.servicesdashboardapi.service.endoflife;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.companieshouse.servicesdashboardapi.model.endoflife.EndOfLifeInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

@Service
public class EndOfLifeService {

   @Value("${endol.api.url}")
   private String endolUrl;

   @Value("${endol.projects}")
   private String[] endolProjects;

   private final RestTemplate restTemplate;

   private final JsonMapper jsonMapper;

   public EndOfLifeService(RestTemplate restTemplate, JsonMapper jsonMapper) {
      this.restTemplate = restTemplate;
      this.jsonMapper = jsonMapper;
   }

   public Map<String, List<EndOfLifeInfo>> fetchEndOfLives() {

      Map<String, List<EndOfLifeInfo>> endofLivesInfo = new HashMap<>();
      List<EndOfLifeInfo> endOfLifeInfoList;

      for (String project : endolProjects) {
         try {
            String url = String.format("%s/%s.json", endolUrl, project);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                  endOfLifeInfoList = jsonMapper.readValue(response.getBody(), new TypeReference<>() {
                  });
                  ApiLogger.info(endOfLifeInfoList.toString());
                  endofLivesInfo.put(project, endOfLifeInfoList);
            }
         } catch (Exception e) {
            ApiLogger.info("Failed to fetch endofLives while processing project " + project + ": " + e.getMessage());
         }
      }

      return endofLivesInfo;
   }
   public String[] getEndolProjects() {
      return endolProjects;
   }
}
