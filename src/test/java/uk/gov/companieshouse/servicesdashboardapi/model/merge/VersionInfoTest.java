package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackMetricsInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class VersionInfoTest {

    @Test
    void shouldSetAndGetAllFields() {
        VersionInfo versionInfo = new VersionInfo();
        DepTrackMetricsInfo metricsInfo = new DepTrackMetricsInfo();
        metricsInfo.setCritical(2);
        metricsInfo.setHigh(4);

        versionInfo.setVersion("1.2.3");
        versionInfo.setUuid("uuid-123");
        versionInfo.setLastBomImport(1724366968904L);
        versionInfo.setDepTrackMetrics(metricsInfo);
        versionInfo.setLang("java");
        versionInfo.setRuntime("21.0.1");

        assertEquals("1.2.3", versionInfo.getVersion());
        assertEquals("uuid-123", versionInfo.getUuid());
        assertEquals(1724366968904L, versionInfo.getLastBomImport());
        assertSame(metricsInfo, versionInfo.getDepTrackMetrics());
        assertEquals("java", versionInfo.getLang());
        assertEquals("21.0.1", versionInfo.getRuntime());
    }

    @Test
    void shouldAllowNullOptionalFields() {
        VersionInfo versionInfo = new VersionInfo();

        versionInfo.setVersion(null);
        versionInfo.setUuid(null);
        versionInfo.setDepTrackMetrics(null);
        versionInfo.setLang(null);
        versionInfo.setRuntime(null);

        assertNull(versionInfo.getVersion());
        assertNull(versionInfo.getUuid());
        assertNull(versionInfo.getDepTrackMetrics());
        assertNull(versionInfo.getLang());
        assertNull(versionInfo.getRuntime());
        assertEquals(0L, versionInfo.getLastBomImport());
    }

    @Test
    void shouldRenderToStringWithAllFields() {
        VersionInfo versionInfo = new VersionInfo();
        DepTrackMetricsInfo metricsInfo = new DepTrackMetricsInfo();
        metricsInfo.setCritical(1);
        metricsInfo.setHigh(2);
        metricsInfo.setMedium(3);
        metricsInfo.setLow(4);
        metricsInfo.setVulnerabilities(10);
        metricsInfo.setComponents(50);
        metricsInfo.setPolicyViolationsTotal(5);
        metricsInfo.setPolicyViolationsWarn(2);
        metricsInfo.setPolicyViolationsFail(1);

        versionInfo.setVersion("2.0.0");
        versionInfo.setUuid("uuid-200");
        versionInfo.setLastBomImport(99L);
        versionInfo.setDepTrackMetrics(metricsInfo);
        versionInfo.setLang("go");
        versionInfo.setRuntime("1.23");

        assertEquals("{v:2.0.0,u:uuid-200,l:99,m:{C:1/H:2/M:3/L:4/v:10/c:50/pT:5/pW:2/pF:1},l:go,r:1.23}", versionInfo.toString());
    }

    @Test
    void shouldRenderToStringWithDefaultValues() {
        VersionInfo versionInfo = new VersionInfo();

        assertEquals("{v:null,u:null,l:0,m:null,l:null,r:null}", versionInfo.toString());
    }
}

