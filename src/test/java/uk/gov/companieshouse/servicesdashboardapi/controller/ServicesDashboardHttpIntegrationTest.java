package uk.gov.companieshouse.servicesdashboardapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ServicesInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.sonar.SonarComponent;
import uk.gov.companieshouse.servicesdashboardapi.model.sonar.SonarProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoConfigRepository;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoProjectInfoRepository;
import uk.gov.companieshouse.servicesdashboardapi.service.ServicesDashboardService;
import uk.gov.companieshouse.servicesdashboardapi.service.deptrack.GetAllProjects;
import uk.gov.companieshouse.servicesdashboardapi.service.endoflife.EndOfLifeService;
import uk.gov.companieshouse.servicesdashboardapi.service.github.GitService;
import uk.gov.companieshouse.servicesdashboardapi.service.sonar.SonarService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ServicesDashboardController.class, HealthCheckController.class})
class ServicesDashboardHttpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServicesDashboardService servicesDashboardService;

    @MockitoBean
    private GetAllProjects servicesDepTrack;

    @MockitoBean
    private SonarService serviceSonar;

    @MockitoBean
    private ServicesInfo servicesInfo;

    @MockitoBean
    private GitService gitService;

    @MockitoBean
    private EndOfLifeService endolService;

    @MockitoBean
    private CustomMongoConfigRepository customMongoConfigRepository;

    @MockitoBean
    private CustomMongoProjectInfoRepository customMongoProjectInfoRepository;

    @Test
    void healthcheckReturnsHealthyResponse() throws Exception {
        mockMvc.perform(get("/services-dashboard/healthcheck"))
                .andExpect(status().isOk())
                .andExpect(content().string("Services Dashboard API Service is healthy"));
    }

    @Test
    void listServicesReturnsEnrichedProjectsAsJson() throws Exception {
        DepTrackProjectInfo dependencyTrackProject = new DepTrackProjectInfo();
        dependencyTrackProject.setName("service-a");
        dependencyTrackProject.setUuid("uuid-a");
        ProjectInfo project = new ProjectInfo();
        project.setName("service-a");
        project.setDepTrackVersions(List.of());
        SonarComponent component = new SonarComponent();
        component.setKey("sonar-service-a");
        component.setMeasures(Map.of("bugs", 2));
        SonarProjectInfo sonar = new SonarProjectInfo();
        sonar.setComponent(component);

        when(servicesDepTrack.fetch()).thenReturn(new ArrayList<>(List.of(dependencyTrackProject)));
        when(servicesInfo.setProjectInfoMap(any())).thenReturn(Map.of("service-a", project));
        when(serviceSonar.fetchMetrics("service-a")).thenReturn(sonar);
        when(gitService.getRepoInfo("service-a")).thenReturn(new uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo());

        mockMvc.perform(get("/services-dashboard/list-services")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("service-a"))
                .andExpect(jsonPath("$[0].sonarKey").value("sonar-service-a"))
                .andExpect(jsonPath("$[0].sonarMetrics.bugs").value(2));
    }
}
