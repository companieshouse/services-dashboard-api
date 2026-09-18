package uk.gov.companieshouse.servicesdashboardapi.service.deptrack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.service.DepTrackGetDataService;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GetAllProjects extends DepTrackGetDataService<List<DepTrackProjectInfo>> {

    @Value("${dt.server.header.totcount}")
    private String headerTotalCount;

    private final JsonMapper jsonMapper;

    public GetAllProjects(JsonMapper jsonMapper,
                          @Value("${dt.server.endpoint.proj}") String endPointValue,
                          RestTemplate restTemplate) {
        super(endPointValue, restTemplate);
        this.jsonMapper = jsonMapper;
    }

    @Override
    public List<DepTrackProjectInfo> fetch() {
        ApiLogger.debug("fetching ...");
        List<DepTrackProjectInfo> allProjects = new ArrayList<>();
        int offset = 0;
        int totalCount = 0;

        // Set the headers
        HttpEntity<String> headers = this.setHeaders(Collections.emptyList());

        List<Map.Entry<String, String>> queryParams = new ArrayList<>(List.of(
                new AbstractMap.SimpleEntry<>("offset", "")
        ));

        do {
            queryParams.getFirst().setValue(String.valueOf(offset));
            String uri = this.setUri(queryParams);

            // Make the GET request with headers
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, headers, String.class);

            List<DepTrackProjectInfo> result;
            ApiLogger.debug(".....SENDING REQ - offset=" + offset);
            try {
                result = jsonMapper.readValue(response.getBody(), new TypeReference<>() {
                });
            } catch (JacksonException e) {
                ApiLogger.error("Failed to parse Dependency Track JSON response", e, Map.of());
                result = Collections.emptyList();
            }
            allProjects.addAll(result);

            // Get the total count from the response headers
            if (totalCount == 0 && response.getHeaders().containsHeader(headerTotalCount)) {
                totalCount = Integer.parseInt(Objects.requireNonNull(response.getHeaders().get(headerTotalCount)).getFirst());
            }

            offset += result.size();

        } while (offset < totalCount);

        return allProjects;
    }
}
