package uk.gov.companieshouse.servicesdashboardapi.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoEndoflifeInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.endoflife.EndofLifeInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ConfigInfo;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigInfoMapperTest {

    @Test
    void shouldMapConfigInfoToMongoConfigInfoWithEndolValues() {
        EndofLifeInfo javaInfo = new EndofLifeInfo();
        javaInfo.setCycle("21");
        javaInfo.setLatest("21.0.4");

        EndofLifeInfo nodeInfo = new EndofLifeInfo();
        nodeInfo.setCycle("20");
        nodeInfo.setLatest("20.17.0");

        ConfigInfo configInfo = new ConfigInfo();
        configInfo.setEndol(Map.of(
                "java", List.of(javaInfo),
                "nodejs", List.of(nodeInfo)
        ));

        MongoConfigInfo result = ConfigInfoMapper.INSTANCE.configInfoToMongoConfigInfo(configInfo);

        assertNotNull(result);
        assertNotNull(result.getEndol());
        assertEquals(2, result.getEndol().size());
        assertEquals("21", result.getEndol().get("java").getFirst().getCycle());
        assertEquals("21.0.4", result.getEndol().get("java").getFirst().getLatest());
        assertEquals("20", result.getEndol().get("nodejs").getFirst().getCycle());
        assertEquals("20.17.0", result.getEndol().get("nodejs").getFirst().getLatest());
        assertNull(result.getId());
        assertNull(result.getLastScan());
    }

    @Test
    void shouldMapSingleEndOfLifeInfoListToMongoEndOfLifeInfoList() {
        EndofLifeInfo info = new EndofLifeInfo();
        info.setCycle("22");
        info.setReleaseDate("2024-03-19");
        info.setLts("false");
        info.setEol("2024-10-31");
        info.setLatest("22.0.2.9.1");
        info.setLatestReleaseDate("2024-07-16");

        List<MongoEndoflifeInfo> result = ConfigInfoMapper.INSTANCE.endofLifeInfoListToMongoEndoflifeInfoList(List.of(info));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("22", result.getFirst().getCycle());
        assertEquals("2024-03-19", result.getFirst().getReleaseDate());
        assertEquals("false", result.getFirst().getLts());
        assertEquals("2024-10-31", result.getFirst().getEol());
        assertEquals("22.0.2.9.1", result.getFirst().getLatest());
        assertEquals("2024-07-16", result.getFirst().getLatestReleaseDate());
    }

    @Test
    void shouldMapEmptyEndOfLifeInfoListToEmptyMongoEndOfLifeInfoList() {
        List<MongoEndoflifeInfo> result = ConfigInfoMapper.INSTANCE.endofLifeInfoListToMongoEndoflifeInfoList(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnNullWhenConfigInfoIsNull() {
        MongoConfigInfo result = ConfigInfoMapper.INSTANCE.configInfoToMongoConfigInfo(null);

        assertNull(result);
    }
}

