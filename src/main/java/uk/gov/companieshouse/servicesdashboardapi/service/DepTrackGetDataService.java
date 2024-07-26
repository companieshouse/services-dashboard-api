package uk.gov.companieshouse.servicesdashboardapi.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import uk.gov.companieshouse.servicesdashboardapi.service.DepTrackGetDataService;

public abstract class DepTrackGetDataService<T> {

   @Value("${dt.server.baseurl}")
   protected String baseUrl;

   @Value("${dt.server.apikey}")
   protected String apiKey;

   @Value("${dt.server.header.apikey}")
   protected String headerApiKey;

   protected String endPoint;

   protected final RestTemplate restTemplate;

   public DepTrackGetDataService(String endPoint) {
      this.restTemplate = new RestTemplate();
      this.endPoint = endPoint;
   }

    protected abstract T fetch();

   public HttpEntity<String> setHeaders(List<Map.Entry<String, String>> keyValuePairs) {
      HttpHeaders headers = new HttpHeaders();
      for (Map.Entry<String, String> pair : keyValuePairs) {
          headers.add(pair.getKey(), pair.getValue());
      }
      // Ensure the "headerApiKey" is always present (without duplication)
      if (!headers.containsKey(this.headerApiKey)) {
          headers.add(this.headerApiKey, this.apiKey);
      }
      return new HttpEntity<>(headers);
   }

   public String setUri(List<Map.Entry<String, String>> queryParams) {
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.baseUrl + this.endPoint);

      for (Map.Entry<String, String> param : queryParams) {
          builder.queryParam(param.getKey(), param.getValue());
      }

      return builder.toUriString();
  }
}
