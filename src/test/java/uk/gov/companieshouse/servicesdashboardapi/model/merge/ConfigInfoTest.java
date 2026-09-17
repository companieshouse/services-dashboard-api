package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.servicesdashboardapi.model.endoflife.EndofLifeInfo;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ConfigInfoTest {

    @Test
    void shouldSetAndGetEndolMapWithMultipleProjects() {
        ConfigInfo configInfo = new ConfigInfo();

        EndofLifeInfo javaInfo = new EndofLifeInfo();
        javaInfo.setCycle("21");
        javaInfo.setLatest("21.0.4");

        EndofLifeInfo nodeInfo = new EndofLifeInfo();
        nodeInfo.setCycle("20");
        nodeInfo.setLatest("20.17.0");

        Map<String, List<EndofLifeInfo>> endol = Map.of(
                "java", List.of(javaInfo),
                "nodejs", List.of(nodeInfo)
        );

        configInfo.setEndol(endol);

        assertSame(endol, configInfo.getEndol());
        assertEquals("21", configInfo.getEndol().get("java").getFirst().getCycle());
        assertEquals("20.17.0", configInfo.getEndol().get("nodejs").getFirst().getLatest());
    }

    @Test
    void shouldAllowNullEndolMap() {
        ConfigInfo configInfo = new ConfigInfo();

        configInfo.setEndol(null);

        assertNull(configInfo.getEndol());
    }

    @Test
    void shouldRenderToStringWithEndolContent() {
        ConfigInfo configInfo = new ConfigInfo();

        EndofLifeInfo javaInfo = new EndofLifeInfo();
        javaInfo.setCycle("21");
        Map<String, List<EndofLifeInfo>> endol = Map.of("java", List.of(javaInfo));
        configInfo.setEndol(endol);

        assertEquals("{endol:{{java=[cycle=21, releaseDate=null, lts:null, eol=null, latest=null, latestReleaseDate:null]}},", configInfo.toString());
    }

    @Test
    void shouldRenderToStringWhenEndolIsNull() {
        ConfigInfo configInfo = new ConfigInfo();

        assertEquals("{endol:{null},", configInfo.toString());
    }
}

