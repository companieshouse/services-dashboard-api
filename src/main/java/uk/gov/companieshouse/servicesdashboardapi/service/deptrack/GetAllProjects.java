package uk.gov.companieshouse.servicesdashboardapi.service.deptrack;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.service.DepTrackGetDataService;

import uk.gov.companieshouse.servicesdashboardapi.utils.JsonMapper;

@Service
public class GetAllProjects extends DepTrackGetDataService <List<DepTrackProjectInfo>>{

   @Value("${dt.server.endpoint.proj}")
   String endPointValue;

   @Value("${dt.server.header.totcount}")
   String headerTotalCount;

    @Autowired
    private JsonMapper jsonMapper;

    public GetAllProjects()  {
        super(null); // Pass a placeholder value, will be overwritten in init method
    }

    // PostConstruct to initialize the endPoint field from the property
    @PostConstruct
    private void init() {
        this.endPoint = endPointValue;
    }

    @Override
    public List<DepTrackProjectInfo> fetch() {
      ApiLogger.debug("fetching ...");
      List<DepTrackProjectInfo> allProjects = new ArrayList<>();
      int offset = 0;
      int totalCount = 0;

      // Set the headers
      HttpEntity<String> headers = this.setHeaders(Collections.emptyList());

      List<Map.Entry<String, String>> queryParams = new ArrayList<>(Arrays.asList(
            new AbstractMap.SimpleEntry<>("offset", "")
        ));

      do {
         queryParams.get(0).setValue(String.valueOf(offset));
         String uri = this.setUri(queryParams);

         // Make the GET request with headers
         ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, headers, String.class);

         List<DepTrackProjectInfo> result;
         ApiLogger.debug(".....SENDING REQ - offset="+offset);
         try {
               result = jsonMapper.readValue(response.getBody(), new TypeReference<List<DepTrackProjectInfo>>() {});
         } catch (IOException e) {
               e.printStackTrace();
               ApiLogger.debug("Failed to parse Dependency Track JSON response");
               result = Collections.emptyList();
         }
         allProjects.addAll(result);

         // Get the total count from the response headers
         if (totalCount == 0 && response.getHeaders().containsKey(headerTotalCount)) {
               totalCount = Integer.parseInt(response.getHeaders().get(headerTotalCount).get(0));
         }

         offset += result.size();

      } while (offset < totalCount);

      return allProjects;
   }
}
