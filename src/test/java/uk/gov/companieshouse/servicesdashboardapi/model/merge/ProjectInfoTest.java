package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectInfoTest {

    @Test
    void shouldSetAndGetAllFields() {
        ProjectInfo projectInfo = new ProjectInfo();

        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setVersion("1.0.0");
        versionInfo.setUuid("uuid-1");

        GitInfo gitInfo = new GitInfo();
        gitInfo.setRepo("https://github.com/companieshouse/service-a");

        List<VersionInfo> versions = List.of(versionInfo);
        Map<String, Integer> sonarMetrics = Map.of("bugs", 2, "vulnerabilities", 1);

        projectInfo.setName("service-a");
        projectInfo.setDepTrackVersions(versions);
        projectInfo.setSonarKey("sonar-service-a");
        projectInfo.setSonarMetrics(sonarMetrics);
        projectInfo.setGitInfo(gitInfo);

        assertEquals("service-a", projectInfo.getName());
        assertSame(versions, projectInfo.getDepTrackVersions());
        assertEquals("sonar-service-a", projectInfo.getSonarKey());
        assertSame(sonarMetrics, projectInfo.getSonarMetrics());
        assertSame(gitInfo, projectInfo.getGitInfo());
    }

    @Test
    void shouldAddVersionWhenVersionListIsInitialized() {
        ProjectInfo projectInfo = new ProjectInfo();
        List<VersionInfo> versions = new ArrayList<>();
        projectInfo.setDepTrackVersions(versions);

        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setVersion("2.0.0");

        projectInfo.addVersion(versionInfo);

        assertEquals(1, projectInfo.getDepTrackVersions().size());
        assertSame(versionInfo, projectInfo.getDepTrackVersions().getFirst());
    }

    @Test
    void shouldThrowWhenAddingVersionWithoutInitializingVersionList() {
        ProjectInfo projectInfo = new ProjectInfo();

        assertThrows(NullPointerException.class, () -> projectInfo.addVersion(new VersionInfo()));
    }

    @Test
    void shouldRenderToStringWithFieldValues() {
        ProjectInfo projectInfo = new ProjectInfo();

        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setVersion("1.0.0");
        versionInfo.setUuid("uuid-1");

        GitInfo gitInfo = new GitInfo();
        gitInfo.setRepo("https://github.com/companieshouse/service-a");

        projectInfo.setName("service-a");
        projectInfo.setDepTrackVersions(List.of(versionInfo));
        projectInfo.setSonarMetrics(Map.of("bugs", 3));
        projectInfo.setGitInfo(gitInfo);
        projectInfo.setSonarKey("sonar-service-a");

        assertEquals(
                "Merge:service-a/[{v:1.0.0,u:uuid-1,l:0,m:null,l:null,r:null}] ({bugs=3}) ({r:https://github.com/companieshouse/service-a l:null o:null sA:null [R:unknown]}) [sonar-service-a]",
                projectInfo.toString()
        );
    }
}

