package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MongoEndoflifeInfoTest {

    @Test
    void shouldSetAndGetAllFields() {
        MongoEndoflifeInfo info = new MongoEndoflifeInfo();

        info.setCycle("21");
        info.setReleaseDate("2023-09-19");
        info.setLts("true");
        info.setEol("2028-09-30");
        info.setLatest("21.0.4");
        info.setLatestReleaseDate("2024-07-16");

        assertEquals("21", info.getCycle());
        assertEquals("2023-09-19", info.getReleaseDate());
        assertEquals("true", info.getLts());
        assertEquals("2028-09-30", info.getEol());
        assertEquals("21.0.4", info.getLatest());
        assertEquals("2024-07-16", info.getLatestReleaseDate());
    }

    @Test
    void shouldAllowNullValuesForAllFields() {
        MongoEndoflifeInfo info = new MongoEndoflifeInfo();

        info.setCycle(null);
        info.setReleaseDate(null);
        info.setLts(null);
        info.setEol(null);
        info.setLatest(null);
        info.setLatestReleaseDate(null);

        assertNull(info.getCycle());
        assertNull(info.getReleaseDate());
        assertNull(info.getLts());
        assertNull(info.getEol());
        assertNull(info.getLatest());
        assertNull(info.getLatestReleaseDate());
    }

    @Test
    void shouldRenderToStringWithAllFieldValues() {
        MongoEndoflifeInfo info = new MongoEndoflifeInfo();

        info.setCycle("22");
        info.setReleaseDate("2024-03-19");
        info.setLts("false");
        info.setEol("2024-10-31");
        info.setLatest("22.0.2.9.1");
        info.setLatestReleaseDate("2024-07-16");

        assertEquals(
                "cycle=22, releaseDate=2024-03-19, lts:false, eol=2024-10-31, latest=22.0.2.9.1, latestReleaseDate:2024-07-16",
                info.toString()
        );
    }

    @Test
    void shouldRenderToStringWithDefaultNullFields() {
        MongoEndoflifeInfo info = new MongoEndoflifeInfo();

        assertEquals(
                "cycle=null, releaseDate=null, lts:null, eol=null, latest=null, latestReleaseDate:null",
                info.toString()
        );
    }
}

