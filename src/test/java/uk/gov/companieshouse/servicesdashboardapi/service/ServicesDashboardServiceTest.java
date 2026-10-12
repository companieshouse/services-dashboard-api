package uk.gov.companieshouse.servicesdashboardapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ServicesInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.VersionInfo;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoProjectInfoRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class ServicesDashboardServiceTest {

    private ServicesDashboardService service;

    private ServicesInfo servicesInfo;

    private CustomMongoProjectInfoRepository repository;

    @BeforeEach
    void setUp() {
        servicesInfo = mock(ServicesInfo.class);
        repository = mock(CustomMongoProjectInfoRepository.class);
        service = new ServicesDashboardService(servicesInfo, repository);
    }

    @Test
    void createsDashboardAndSavesMappedProjects() {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setVersion("1.0.0");
        versionInfo.setUuid("uuid-1");

        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setName("service-a");
        projectInfo.setDepTrackVersions(List.of(versionInfo));
        projectInfo.setSonarKey("sonar-service-a");
        projectInfo.setSonarMetrics(Map.of("bugs", 3));

        when(servicesInfo.getProjectInfoMap()).thenReturn(Map.of("service-a", projectInfo));

        service.createServicesDashboard();

        ArgumentCaptor<List<MongoProjectInfo>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveProjectInfos(captor.capture());

        List<MongoProjectInfo> savedProjects = captor.getValue();
        assertEquals(1, savedProjects.size());
        assertEquals("service-a", savedProjects.getFirst().getName());
        assertEquals("sonar-service-a", savedProjects.getFirst().getSonarKey());
        assertEquals(3, savedProjects.getFirst().getSonarMetrics().get("bugs"));
        assertEquals("1.0.0", savedProjects.getFirst().getVersions().getFirst().getVersion());
        assertEquals("uuid-1", savedProjects.getFirst().getVersions().getFirst().getUuid());
    }

    @Test
    void createsDashboardAndSavesEmptyListWhenNoProjectsExist() {
        when(servicesInfo.getProjectInfoMap()).thenReturn(Collections.emptyMap());

        service.createServicesDashboard();

        ArgumentCaptor<List<MongoProjectInfo>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveProjectInfos(captor.capture());
        assertEquals(0, captor.getValue().size());
    }

    @Test
    void throwsExceptionAndDoesNotSaveWhenProjectInfoMapIsNull() {
        when(servicesInfo.getProjectInfoMap()).thenReturn(null);

        assertThrows(NullPointerException.class, service::createServicesDashboard);
        verify(repository, never()).saveProjectInfos(org.mockito.ArgumentMatchers.anyList());
    }

}
