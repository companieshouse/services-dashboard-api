package uk.gov.companieshouse.servicesdashboardapi.service.endoflife;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.type.TypeReference;

import uk.gov.companieshouse.servicesdashboardapi.model.endoflife.EndofLifeInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.utils.CustomJsonMapper;

@Service
public class EndoflifeService {

   @Value("${endol.api.url}")
   private String endolUrl;

   @Value("${endol.projects}")
   private String[] endolProjects;

   @Autowired
   private RestTemplate restTemplate;

   @Autowired
   private CustomJsonMapper jsonMapper;

   public Map<String, List<EndofLifeInfo>> fetcEndofLives() {

      Map<String, List<EndofLifeInfo>> endofLivesInfo = new HashMap<>();
      List<EndofLifeInfo> endOfLifeInfoList;

      for (String project : endolProjects) {
         try {
            String url = String.format("%s/%s.json", endolUrl, project);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                  endOfLifeInfoList = jsonMapper.readValue(response.getBody(), new TypeReference<List<EndofLifeInfo>>() {});
                  ApiLogger.info("" + endOfLifeInfoList);
                  endofLivesInfo.put(project, endOfLifeInfoList);
            }
         } catch (Exception e) {
            ApiLogger.errorContext("Failed to fetch endofLives while processing project " + project + ": " + e.getMessage(), e);
         }
      }

      return endofLivesInfo;
   }
   public String[] getEndolProjects() {
      return endolProjects;
   }
}
