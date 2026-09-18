package uk.gov.companieshouse.servicesdashboardapi.service.endoflife;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.companieshouse.servicesdashboardapi.model.endoflife.EndofLifeInfo;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class EndOfLifeServiceTest {

    private RestTemplate restTemplate;
    private JsonMapper jsonMapper;
    private EndOfLifeService endOfLifeService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        jsonMapper = mock(JsonMapper.class);
        endOfLifeService = new EndOfLifeService(restTemplate, jsonMapper);

        ReflectionTestUtils.setField(endOfLifeService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(endOfLifeService, "jsonMapper", jsonMapper);
        ReflectionTestUtils.setField(endOfLifeService, "endolUrl", "https://endoflife.example.com/api");
    }

    @Test
    void fetchEndOfLifeInformationReturnsEntriesForProjectsWithSuccessfulResponses() throws Exception {
        ReflectionTestUtils.setField(endOfLifeService, "endolProjects", new String[]{"java", "nodejs"});

        String javaUrl = "https://endoflife.example.com/api/java.json";
        String nodeUrl = "https://endoflife.example.com/api/nodejs.json";
        when(restTemplate.getForEntity(javaUrl, String.class)).thenReturn(ResponseEntity.ok("java-json"));
        when(restTemplate.getForEntity(nodeUrl, String.class)).thenReturn(ResponseEntity.ok("node-json"));

        EndofLifeInfo javaInfo = new EndofLifeInfo();
        javaInfo.setCycle("21");
        EndofLifeInfo nodeInfo = new EndofLifeInfo();
        nodeInfo.setCycle("20");
        List<EndofLifeInfo> javaList = List.of(javaInfo);
        List<EndofLifeInfo> nodeList = List.of(nodeInfo);

        when(jsonMapper.readValue(eq("java-json"), any(TypeReference.class))).thenReturn(javaList);
        when(jsonMapper.readValue(eq("node-json"), any(TypeReference.class))).thenReturn(nodeList);

        Map<String, List<EndofLifeInfo>> result = endOfLifeService.fetchEndOfLives();

        assertEquals(2, result.size());
        assertEquals(javaList, result.get("java"));
        assertEquals(nodeList, result.get("nodejs"));
    }

    @Test
    void fetchEndOfLifeInformationSkipsProjectWhenResponseIsNotSuccessful() throws Exception {
        ReflectionTestUtils.setField(endOfLifeService, "endolProjects", new String[]{"java"});

        String javaUrl = "https://endoflife.example.com/api/java.json";
        when(restTemplate.getForEntity(javaUrl, String.class))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body("not-found"));

        Map<String, List<EndofLifeInfo>> result = endOfLifeService.fetchEndOfLives();

        assertTrue(result.isEmpty());
        verify(jsonMapper, never()).readValue(any(String.class), any(TypeReference.class));
    }

    @Test
    void fetchEndOfLifeInformationContinuesWhenOneProjectRequestFails() throws Exception {
        ReflectionTestUtils.setField(endOfLifeService, "endolProjects", new String[]{"java", "nodejs"});

        String javaUrl = "https://endoflife.example.com/api/java.json";
        String nodeUrl = "https://endoflife.example.com/api/nodejs.json";

        when(restTemplate.getForEntity(javaUrl, String.class)).thenThrow(new RuntimeException("upstream down"));
        when(restTemplate.getForEntity(nodeUrl, String.class)).thenReturn(ResponseEntity.ok("node-json"));

        EndofLifeInfo nodeInfo = new EndofLifeInfo();
        nodeInfo.setCycle("20");
        List<EndofLifeInfo> nodeList = List.of(nodeInfo);
        when(jsonMapper.readValue(eq("node-json"), any(TypeReference.class))).thenReturn(nodeList);

        Map<String, List<EndofLifeInfo>> result = endOfLifeService.fetchEndOfLives();

        assertEquals(1, result.size());
        assertEquals(nodeList, result.get("nodejs"));
    }

    @Test
    void fetchEndOfLifeInformationContinuesWhenJsonParsingFailsForAProject() throws Exception {
        ReflectionTestUtils.setField(endOfLifeService, "endolProjects", new String[]{"java", "nodejs"});

        String javaUrl = "https://endoflife.example.com/api/java.json";
        String nodeUrl = "https://endoflife.example.com/api/nodejs.json";
        when(restTemplate.getForEntity(javaUrl, String.class)).thenReturn(ResponseEntity.ok("java-json"));
        when(restTemplate.getForEntity(nodeUrl, String.class)).thenReturn(ResponseEntity.ok("node-json"));

        when(jsonMapper.readValue(eq("java-json"), any(TypeReference.class)))
                .thenThrow(new RuntimeException("invalid json"));
        EndofLifeInfo nodeInfo = new EndofLifeInfo();
        nodeInfo.setCycle("20");
        List<EndofLifeInfo> nodeList = List.of(nodeInfo);
        when(jsonMapper.readValue(eq("node-json"), any(TypeReference.class))).thenReturn(nodeList);

        Map<String, List<EndofLifeInfo>> result = endOfLifeService.fetchEndOfLives();

        assertEquals(1, result.size());
        assertEquals(nodeList, result.get("nodejs"));
    }

    @Test
    void fetchEndOfLifeInformationReturnsEmptyMapWhenNoProjectsConfigured() {
        ReflectionTestUtils.setField(endOfLifeService, "endolProjects", new String[]{});

        Map<String, List<EndofLifeInfo>> result = endOfLifeService.fetchEndOfLives();

        assertTrue(result.isEmpty());
    }

    @Test
    void getEndolProjectsReturnsConfiguredProjects() {
        String[] projects = new String[]{"java", "nodejs"};
        ReflectionTestUtils.setField(endOfLifeService, "endolProjects", projects);

        assertArrayEquals(projects, endOfLifeService.getEndolProjects());
    }

}
