package uk.gov.companieshouse.servicesdashboardapi.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackMetricsInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackTag;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.VersionInfo;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MergeInfoMapperTest {

    private static DepTrackProjectInfo depTrackProjectInfo(String name,
                                                           String version,
                                                           String uuid,
                                                           long lastBomImport,
                                                           List<DepTrackTag> tags,
                                                           DepTrackMetricsInfo metrics) {
        DepTrackProjectInfo info = new DepTrackProjectInfo();
        info.setName(name);
        info.setVersion(version);
        info.setUuid(uuid);
        info.setLastBomImport(lastBomImport);
        info.setTags(tags);
        info.setMetrics(metrics);
        return info;
    }

    private static DepTrackTag tag(String value) {
        DepTrackTag tag = new DepTrackTag();
        tag.setName(value);
        return tag;
    }

    @Test
    void shouldMapDepTrackProjectInfoToVersionInfoIncludingTagsAndMetrics() {
        DepTrackMetricsInfo metrics = new DepTrackMetricsInfo();
        metrics.setCritical(2);
        metrics.setHigh(4);
        metrics.setPolicyViolationsFail(1);

        DepTrackProjectInfo depTrackProjectInfo = depTrackProjectInfo(
                "service-a",
                "1.0.0",
                "uuid-1",
                1724366968904L,
                List.of(tag("lang:java"), tag("runtime:21.0.1")),
                metrics
        );

        VersionInfo result = MergeInfoMapper.INSTANCE.mapToVersionInfo(depTrackProjectInfo);

        assertNotNull(result);
        assertEquals("1.0.0", result.getVersion());
        assertEquals("uuid-1", result.getUuid());
        assertEquals(1724366968904L, result.getLastBomImport());
        assertEquals("java", result.getLang());
        assertEquals("21.0.1", result.getRuntime());
        assertNotNull(result.getDepTrackMetrics());
        assertEquals(2, result.getDepTrackMetrics().getCritical());
        assertEquals(4, result.getDepTrackMetrics().getHigh());
        assertEquals(1, result.getDepTrackMetrics().getPolicyViolationsFail());
    }

    @Test
    void shouldMapDepTrackProjectInfoToProjectInfoWithOnlyNameAndIgnoredFieldsNull() {
        DepTrackProjectInfo depTrackProjectInfo = depTrackProjectInfo(
                "service-a",
                "1.0.0",
                "uuid-1",
                1L,
                List.of(),
                null
        );

        ProjectInfo result = MergeInfoMapper.INSTANCE.mapToProjectInfo(depTrackProjectInfo);

        assertNotNull(result);
        assertEquals("service-a", result.getName());
        assertNull(result.getDepTrackVersions());
        assertNull(result.getSonarKey());
        assertNull(result.getSonarMetrics());
        assertNull(result.getGitInfo());
    }

    @Test
    void shouldGroupProjectsByNameAndAggregateVersions() {
        DepTrackProjectInfo first = depTrackProjectInfo("service-a", "1.0.0", "uuid-1", 100L,
                List.of(tag("lang:java"), tag("runtime:21")), null);
        DepTrackProjectInfo second = depTrackProjectInfo("service-a", "1.1.0", "uuid-2", 200L,
                List.of(tag("lang:java"), tag("runtime:22")), null);
        DepTrackProjectInfo third = depTrackProjectInfo("service-b", "2.0.0", "uuid-3", 300L,
                List.of(tag("lang:go"), tag("runtime:1.23")), null);

        Map<String, ProjectInfo> result = MergeInfoMapper.INSTANCE.mapDepTrackListToProjectInfoMap(List.of(first, second, third));

        assertEquals(2, result.size());
        assertTrue(result.containsKey("service-a"));
        assertTrue(result.containsKey("service-b"));

        ProjectInfo serviceA = result.get("service-a");
        assertNotNull(serviceA.getDepTrackVersions());
        assertEquals(2, serviceA.getDepTrackVersions().size());
        assertEquals("1.0.0", serviceA.getDepTrackVersions().get(0).getVersion());
        assertEquals("1.1.0", serviceA.getDepTrackVersions().get(1).getVersion());

        ProjectInfo serviceB = result.get("service-b");
        assertNotNull(serviceB.getDepTrackVersions());
        assertEquals(1, serviceB.getDepTrackVersions().size());
        assertEquals("2.0.0", serviceB.getDepTrackVersions().getFirst().getVersion());
    }

    @Test
    void shouldReturnEmptyMapWhenProjectListIsEmpty() {
        Map<String, ProjectInfo> result = MergeInfoMapper.INSTANCE.mapDepTrackListToProjectInfoMap(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowWhenProjectListIsNull() {
        assertThrows(NullPointerException.class, () -> MergeInfoMapper.INSTANCE.mapDepTrackListToProjectInfoMap(null));
    }

    @Test
    void shouldExtractTagValuesByPrefix() {
        List<DepTrackTag> tags = List.of(
                tag("lang:java"),
                tag("runtime:21.0.1"),
                tag("other:value")
        );

        assertEquals("java", MergeInfoMapper.INSTANCE.getLangTagValue(tags));
        assertEquals("21.0.1", MergeInfoMapper.INSTANCE.getRuntimeTagValue(tags));
        assertEquals("value", MergeInfoMapper.INSTANCE.getTagValue(tags, "other"));
    }

    @Test
    void shouldReturnEmptyStringWhenTagsAreMissingOrPrefixNotFound() {
        assertEquals("", MergeInfoMapper.INSTANCE.getLangTagValue(null));
        assertEquals("", MergeInfoMapper.INSTANCE.getRuntimeTagValue(List.of()));
        assertEquals("", MergeInfoMapper.INSTANCE.getTagValue(List.of(tag("lang:java")), "runtime"));
    }
}

