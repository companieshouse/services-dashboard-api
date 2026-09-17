package uk.gov.companieshouse.servicesdashboardapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.endoflife.EndofLifeInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicesDashboardControllerTest {

    @Mock
    private ServicesDashboardService servicesDashboardService;

    @Mock
    private GetAllProjects servicesDepTrack;

    @Mock
    private SonarService serviceSonar;

    @Mock
    private ServicesInfo servicesInfo;

    @Mock
    private GitService gitService;

    @Mock
    private EndOfLifeService endolService;

    @Mock
    private CustomMongoConfigRepository customMongoConfigRepository;

    @Mock
    private CustomMongoProjectInfoRepository customMongoProjectInfoRepository;

    @InjectMocks
    private ServicesDashboardController controller;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "deepScanEnabled", true);
        ReflectionTestUtils.setField(controller, "awsEnvs", new String[]{"dev", "staging", "prod"});
        ReflectionTestUtils.setField(controller, "serviceSonar", serviceSonar);
        ReflectionTestUtils.setField(controller, "servicesInfo", servicesInfo);
        ReflectionTestUtils.setField(controller, "gitService", gitService);
        ReflectionTestUtils.setField(controller, "endolService", endolService);
        ReflectionTestUtils.setField(controller, "customMongoConfigRepository", customMongoConfigRepository);
        ReflectionTestUtils.setField(controller, "customMongoProjectInfoRepository", customMongoProjectInfoRepository);
    }

    @Test
    void listServicesReturnsProjectInfoList() {
        List<DepTrackProjectInfo> depTrackProjects = createMockDepTrackProjects();
        Map<String, ProjectInfo> projectInfoMap = createMockProjectInfoMap();

        when(servicesDepTrack.fetch()).thenReturn(depTrackProjects);
        when(servicesInfo.setProjectInfoMap(depTrackProjects)).thenReturn(projectInfoMap);
        when(serviceSonar.fetchMetrics(anyString())).thenReturn(createMockSonarProjectInfo());
        when(gitService.getRepoInfo(anyString())).thenReturn(createMockGitInfo());

        ResponseEntity<List<ProjectInfo>> response = controller.listServices();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(projectInfoMap.size());
    }

    @Test
    void listServicesReturnsEmptyListWhenNoProjectsAvailable() {
        when(servicesDepTrack.fetch()).thenReturn(new ArrayList<>());
        when(servicesInfo.setProjectInfoMap(new ArrayList<>())).thenReturn(new HashMap<>());

        ResponseEntity<List<ProjectInfo>> response = controller.listServices();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void sourceEndolReturnsOkStatus() {
        Map<String, List<EndofLifeInfo>> endolMap = new HashMap<>();
        when(endolService.fetchEndOfLives()).thenReturn(endolMap);

        ResponseEntity<String> response = controller.sourceEndol();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Endol ok");
    }

    @Test
    void sourceEndolPersistsEndofLifeInfoToMongo() {
        Map<String, List<EndofLifeInfo>> endolMap = new HashMap<>();
        when(endolService.fetchEndOfLives()).thenReturn(endolMap);

        controller.sourceEndol();

        verify(customMongoConfigRepository).saveConfigInfo(any(MongoConfigInfo.class));
    }

    @Test
    void filterListRemovesExistingProjectsWhenDeepScanDisabled() {
        List<DepTrackProjectInfo> projects = createMockDepTrackProjects();
        ReflectionTestUtils.setField(controller, "deepScanEnabled", false);

        when(customMongoProjectInfoRepository.existsByUuid("project1", "uuid1")).thenReturn(true);
        when(customMongoProjectInfoRepository.existsByUuid("project2", "uuid2")).thenReturn(false);

        controller.filterList(projects);

        assertThat(projects).hasSize(1);
    }

    @Test
    void filterListKeepsAllProjectsWhenDeepScanEnabled() {
        List<DepTrackProjectInfo> projects = createMockDepTrackProjects();
        ReflectionTestUtils.setField(controller, "deepScanEnabled", true);

        controller.filterList(projects);

        assertThat(projects).hasSize(2);
        verify(customMongoProjectInfoRepository, never()).existsByUuid(anyString(), anyString());
    }

    @Test
    void filterListHandlesEmptyProjectList() {
        List<DepTrackProjectInfo> projects = new ArrayList<>();
        ReflectionTestUtils.setField(controller, "deepScanEnabled", false);

        controller.filterList(projects);

        assertThat(projects).isEmpty();
    }

    @Test
    void loadListServicesEnrichesProjectsWithSonarMetrics() {
        List<DepTrackProjectInfo> depTrackProjects = createMockDepTrackProjects();
        Map<String, ProjectInfo> projectInfoMap = createMockProjectInfoMap();
        SonarProjectInfo sonarInfo = createMockSonarProjectInfo();

        when(servicesDepTrack.fetch()).thenReturn(depTrackProjects);
        when(servicesInfo.setProjectInfoMap(depTrackProjects)).thenReturn(projectInfoMap);
        when(serviceSonar.fetchMetrics(anyString())).thenReturn(sonarInfo);
        when(gitService.getRepoInfo(anyString())).thenReturn(createMockGitInfo());

        Map<String, ProjectInfo> result = controller.loadListServices();

        assertThat(result).isNotEmpty();
        result.values().forEach(project -> {
            assertThat(project.getSonarKey()).isNotNull();
            assertThat(project.getSonarMetrics()).isNotNull();
        });
    }

    @Test
    void loadListServicesEnrichesProjectsWithGitInfo() {
        List<DepTrackProjectInfo> depTrackProjects = createMockDepTrackProjects();
        Map<String, ProjectInfo> projectInfoMap = createMockProjectInfoMap();
        GitInfo gitInfo = createMockGitInfo();

        when(servicesDepTrack.fetch()).thenReturn(depTrackProjects);
        when(servicesInfo.setProjectInfoMap(depTrackProjects)).thenReturn(projectInfoMap);
        when(serviceSonar.fetchMetrics(anyString())).thenReturn(createMockSonarProjectInfo());
        when(gitService.getRepoInfo(anyString())).thenReturn(gitInfo);

        Map<String, ProjectInfo> result = controller.loadListServices();

        assertThat(result).isNotEmpty();
        result.values().forEach(project -> assertThat(project.getGitInfo()).isEqualTo(gitInfo));
    }

    @Test
    void loadListServicesHandlesNullSonarComponent() {
        List<DepTrackProjectInfo> depTrackProjects = createMockDepTrackProjects();
        Map<String, ProjectInfo> projectInfoMap = createMockProjectInfoMap();
        SonarProjectInfo sonarInfoWithNullComponent = new SonarProjectInfo();
        sonarInfoWithNullComponent.setComponent(null);

        when(servicesDepTrack.fetch()).thenReturn(depTrackProjects);
        when(servicesInfo.setProjectInfoMap(depTrackProjects)).thenReturn(projectInfoMap);
        when(serviceSonar.fetchMetrics(anyString())).thenReturn(sonarInfoWithNullComponent);
        when(gitService.getRepoInfo(anyString())).thenReturn(createMockGitInfo());

        Map<String, ProjectInfo> result = controller.loadListServices();

        assertThat(result).isNotEmpty();
        result.values().forEach(project -> assertThat(project.getSonarKey()).isNull());
    }

    @Test
    void loadListEolFetchesAndPersistsEndofLifeData() {
        Map<String, List<EndofLifeInfo>> endolMap = new HashMap<>();
        when(endolService.fetchEndOfLives()).thenReturn(endolMap);

        controller.loadListEol();

        verify(endolService).fetchEndOfLives();
        verify(customMongoConfigRepository).saveConfigInfo(any(MongoConfigInfo.class));
    }

    @Test
    void loadAllInfoWithDeepScanTrueLoadsAllData() {
        List<DepTrackProjectInfo> depTrackProjects = createMockDepTrackProjects();
        Map<String, ProjectInfo> projectInfoMap = createMockProjectInfoMap();

        when(servicesDepTrack.fetch()).thenReturn(depTrackProjects);
        when(servicesInfo.setProjectInfoMap(depTrackProjects)).thenReturn(projectInfoMap);
        when(serviceSonar.fetchMetrics(anyString())).thenReturn(createMockSonarProjectInfo());
        when(gitService.getRepoInfo(anyString())).thenReturn(createMockGitInfo());
        when(endolService.fetchEndOfLives()).thenReturn(new HashMap<>());

        controller.loadAllInfo(true);

        verify(servicesDashboardService).createServicesDashboard();
        verify(customMongoConfigRepository).saveConfigInfo(any(MongoConfigInfo.class));
        assertThat((Boolean) ReflectionTestUtils.getField(controller, "deepScanEnabled")).isTrue();
    }

    @Test
    void loadAllInfoWithDeepScanFalseFiltersExistingProjects() {
        List<DepTrackProjectInfo> depTrackProjects = createMockDepTrackProjects();
        Map<String, ProjectInfo> projectInfoMap = createMockProjectInfoMap();

        when(servicesDepTrack.fetch()).thenReturn(depTrackProjects);
        when(servicesInfo.setProjectInfoMap(any())).thenReturn(projectInfoMap);
        when(serviceSonar.fetchMetrics(anyString())).thenReturn(createMockSonarProjectInfo());
        when(gitService.getRepoInfo(anyString())).thenReturn(createMockGitInfo());
        when(customMongoProjectInfoRepository.existsByUuid("project1", "uuid1")).thenReturn(true);
        when(endolService.fetchEndOfLives()).thenReturn(new HashMap<>());

        controller.loadAllInfo(false);

        assertThat((Boolean) ReflectionTestUtils.getField(controller, "deepScanEnabled")).isFalse();
        verify(customMongoConfigRepository).saveConfigInfo(any(MongoConfigInfo.class));
    }

    private List<DepTrackProjectInfo> createMockDepTrackProjects() {
        List<DepTrackProjectInfo> projects = new ArrayList<>();
        DepTrackProjectInfo project1 = new DepTrackProjectInfo();
        project1.setName("project1");
        project1.setUuid("uuid1");

        DepTrackProjectInfo project2 = new DepTrackProjectInfo();
        project2.setName("project2");
        project2.setUuid("uuid2");

        projects.add(project1);
        projects.add(project2);
        return projects;
    }

    private Map<String, ProjectInfo> createMockProjectInfoMap() {
        Map<String, ProjectInfo> map = new HashMap<>();
        map.put("project1", new ProjectInfo());
        map.put("project2", new ProjectInfo());
        return map;
    }

    private SonarProjectInfo createMockSonarProjectInfo() {
        SonarProjectInfo info = new SonarProjectInfo();
        SonarComponent component = new SonarComponent();
        component.setKey("test-key");
        component.setMeasures(new HashMap<>());
        info.setComponent(component);
        return info;
    }

    private GitInfo createMockGitInfo() {
        return new GitInfo();
    }
}
