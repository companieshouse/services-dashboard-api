package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackTag;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicesInfoTest {

    private static DepTrackProjectInfo depTrackProject(String name,
                                                       String version,
                                                       String uuid,
                                                       long lastBomImport,
                                                       String langTag,
                                                       String runtimeTag) {
        DepTrackTag lang = new DepTrackTag();
        lang.setName(langTag);
        DepTrackTag runtime = new DepTrackTag();
        runtime.setName(runtimeTag);

        DepTrackProjectInfo projectInfo = new DepTrackProjectInfo();
        projectInfo.setName(name);
        projectInfo.setVersion(version);
        projectInfo.setUuid(uuid);
        projectInfo.setLastBomImport(lastBomImport);
        projectInfo.setTags(List.of(lang, runtime));
        return projectInfo;
    }

    @Test
    void shouldMapProjectsByNameAndAggregateVersions() {
        ServicesInfo servicesInfo = new ServicesInfo();

        DepTrackProjectInfo first = depTrackProject("service-a", "1.0.0", "uuid-1", 100L, "lang:java", "runtime:21");
        DepTrackProjectInfo second = depTrackProject("service-a", "1.1.0", "uuid-2", 200L, "lang:java", "runtime:21");
        DepTrackProjectInfo third = depTrackProject("service-b", "2.0.0", "uuid-3", 300L, "lang:go", "runtime:1.23");

        Map<String, ProjectInfo> result = servicesInfo.setProjectInfoMap(List.of(first, second, third));

        assertSame(result, servicesInfo.getProjectInfoMap());
        assertEquals(2, result.size());
        assertTrue(result.containsKey("service-a"));
        assertTrue(result.containsKey("service-b"));

        ProjectInfo serviceA = result.get("service-a");
        assertEquals("service-a", serviceA.getName());
        assertNotNull(serviceA.getDepTrackVersions());
        assertEquals(2, serviceA.getDepTrackVersions().size());
        assertEquals("1.0.0", serviceA.getDepTrackVersions().get(0).getVersion());
        assertEquals("1.1.0", serviceA.getDepTrackVersions().get(1).getVersion());
        assertEquals("java", serviceA.getDepTrackVersions().get(0).getLang());
        assertEquals("21", serviceA.getDepTrackVersions().get(0).getRuntime());

        ProjectInfo serviceB = result.get("service-b");
        assertEquals(1, serviceB.getDepTrackVersions().size());
        assertEquals("go", serviceB.getDepTrackVersions().getFirst().getLang());
        assertEquals("1.23", serviceB.getDepTrackVersions().getFirst().getRuntime());
    }

    @Test
    void shouldReturnEmptyMapWhenDepTrackListIsEmpty() {
        ServicesInfo servicesInfo = new ServicesInfo();

        Map<String, ProjectInfo> result = servicesInfo.setProjectInfoMap(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertSame(result, servicesInfo.getProjectInfoMap());
    }

    @Test
    void shouldThrowWhenDepTrackListIsNull() {
        ServicesInfo servicesInfo = new ServicesInfo();

        assertThrows(NullPointerException.class, () -> servicesInfo.setProjectInfoMap(null));
    }
}

