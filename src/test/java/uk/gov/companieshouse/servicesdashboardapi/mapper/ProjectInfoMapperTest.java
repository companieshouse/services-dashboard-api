package uk.gov.companieshouse.servicesdashboardapi.mapper;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoGitReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoVersionInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackMetricsInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitReleaseInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.VersionInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectInfoMapperTest {

    @NotNull
    private static ProjectInfo initializeProjectInfo() {
        GitInfo gitInfo = new GitInfo();
        gitInfo.setRepo("https://github.com/companieshouse/service-a");
        gitInfo.setLang("Java");
        gitInfo.setOwner("team-photon");
        gitInfo.setServiceArea("Common Components");

        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setName("service-a");
        projectInfo.setDepTrackVersions(List.of(initialiseVersionInfo()));
        projectInfo.setSonarKey("sonar-service-a");
        projectInfo.setSonarMetrics(Map.of("bugs", 4));
        projectInfo.setGitInfo(gitInfo);
        return projectInfo;
    }

    @NotNull
    private static VersionInfo initialiseVersionInfo() {
        DepTrackMetricsInfo metrics = new DepTrackMetricsInfo();
        metrics.setCritical(1);
        metrics.setHigh(2);
        metrics.setPolicyViolationsFail(3);

        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setVersion("1.0.0");
        versionInfo.setUuid("uuid-1");
        versionInfo.setLastBomImport(1724366968904L);
        versionInfo.setDepTrackMetrics(metrics);
        versionInfo.setLang("java");
        versionInfo.setRuntime("21.0.1");

        return versionInfo;
    }

    @SuppressWarnings("SameParameterValue")
    private static Date parseUtcDate(String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(date);
    }

    @Test
    void shouldMapProjectInfoToMongoProjectInfoIncludingNestedFields() {
        ProjectInfo projectInfo = initializeProjectInfo();

        MongoProjectInfo result = ProjectInfoMapper.INSTANCE.mapProjectInfoToMongoProjectInfo(projectInfo);

        assertNotNull(result);
        assertEquals("service-a", result.getName());
        assertEquals("sonar-service-a", result.getSonarKey());
        assertEquals(4, result.getSonarMetrics().get("bugs"));
        assertNotNull(result.getGitInfo());
        assertEquals("https://github.com/companieshouse/service-a", result.getGitInfo().getRepo());
        assertEquals("team-photon", result.getGitInfo().getOwner());

        assertNotNull(result.getVersions());
        assertEquals(1, result.getVersions().size());
        MongoVersionInfo mappedVersion = result.getVersions().getFirst();
        assertEquals("1.0.0", mappedVersion.getVersion());
        assertEquals("uuid-1", mappedVersion.getUuid());
        assertEquals(new Date(1724366968904L), mappedVersion.getLastBomImport());
        assertEquals("java", mappedVersion.getLang());
        assertEquals("21.0.1", mappedVersion.getRuntime());
        assertNotNull(mappedVersion.getMetrics());
        assertEquals(1, mappedVersion.getMetrics().getCritical());
        assertEquals(2, mappedVersion.getMetrics().getHigh());
        assertEquals(3, mappedVersion.getMetrics().getPolicyViolationsFail());
    }

    @Test
    void shouldMapProjectInfoListToMongoProjectInfoList() {
        ProjectInfo first = new ProjectInfo();
        first.setName("service-a");

        ProjectInfo second = new ProjectInfo();
        second.setName("service-b");

        List<MongoProjectInfo> result = ProjectInfoMapper.INSTANCE.mapProjectInfoList(List.of(first, second));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("service-a", result.get(0).getName());
        assertEquals("service-b", result.get(1).getName());
    }

    @Test
    void shouldMapProjectInfoMapToMongoProjectInfoList() {
        ProjectInfo first = new ProjectInfo();
        first.setName("service-a");

        ProjectInfo second = new ProjectInfo();
        second.setName("service-b");

        Map<String, ProjectInfo> projectInfoMap = new LinkedHashMap<>();
        projectInfoMap.put("service-a", first);
        projectInfoMap.put("service-b", second);

        List<MongoProjectInfo> result = ProjectInfoMapper.INSTANCE.mapProjectInfoMap(projectInfoMap);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("service-a", result.get(0).getName());
        assertEquals("service-b", result.get(1).getName());
    }

    @Test
    void shouldConvertGitReleaseDateToUtcDateWhenDateStartsWithIsoDay() {
        GitReleaseInfo releaseInfo = new GitReleaseInfo();
        releaseInfo.setVersion("v1.2.3");
        releaseInfo.setDate("2026-01-23T10:20:30Z");

        MongoGitReleaseInfo result = ProjectInfoMapper.INSTANCE.toMongoGitReleaseInfo(releaseInfo);

        assertNotNull(result);
        assertEquals("v1.2.3", result.getVersion());
        assertEquals(ProjectInfoMapper.INSTANCE.stringToDate("2026-01-23"), result.getDate());
    }

    @Test
    void shouldFallbackToDefaultDateWhenGitReleaseDateIsInvalidOrNull() {
        assertEquals(ProjectInfoMapper.INSTANCE.stringToDate("1970-01-01"), ProjectInfoMapper.INSTANCE.stringToDate("not-a-date"));
        assertEquals(ProjectInfoMapper.INSTANCE.stringToDate("1970-01-01"), ProjectInfoMapper.INSTANCE.stringToDate(null));
    }

    @Test
    void shouldParseExactIsoDateString() throws Exception {
        Date result = ProjectInfoMapper.INSTANCE.stringToDate("2026-01-23");

        assertEquals(parseUtcDate("2026-01-23"), result);
    }

    @Test
    void shouldParseDatePrefixWhenStringContainsAdditionalSuffix() throws Exception {
        Date result = ProjectInfoMapper.INSTANCE.stringToDate("2026-01-23T17:45:01+01:00");

        assertEquals(parseUtcDate("2026-01-23"), result);
    }

    @Test
    void shouldFallbackToDefaultDateWhenInputIsBlank() {
        assertEquals(ProjectInfoMapper.INSTANCE.stringToDate("1970-01-01"), ProjectInfoMapper.INSTANCE.stringToDate(""));
    }

    @Test
    void shouldReturnNullWhenGitReleaseInfoIsNull() {
        assertNull(ProjectInfoMapper.INSTANCE.toMongoGitReleaseInfo(null));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowWhenProjectInfoCollectionsAreNull() {
        assertThrows(NullPointerException.class, () -> ProjectInfoMapper.INSTANCE.mapProjectInfoList(null));
        assertThrows(NullPointerException.class, () -> ProjectInfoMapper.INSTANCE.mapProjectInfoMap(null));
    }

    @Test
    void shouldMapEmptyCollectionsToEmptyResults() {
        assertTrue(ProjectInfoMapper.INSTANCE.mapProjectInfoList(List.of()).isEmpty());
        assertTrue(ProjectInfoMapper.INSTANCE.mapProjectInfoMap(Map.of()).isEmpty());
        assertTrue(ProjectInfoMapper.INSTANCE.toMongoGitReleaseInfoList(List.of()).isEmpty());
    }
}

