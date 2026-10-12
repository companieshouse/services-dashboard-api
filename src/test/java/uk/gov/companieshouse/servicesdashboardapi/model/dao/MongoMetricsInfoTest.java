package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MongoMetricsInfoTest {

    @Test
    void shouldCreateMongoMetricsInfoWithValues() {
        MongoMetricsInfo mongoMetrics = new MongoMetricsInfo();
        mongoMetrics.setCritical(1);
        mongoMetrics.setHigh(2);
        mongoMetrics.setMedium(3);
        mongoMetrics.setLow(4);
        mongoMetrics.setVulnerabilities(10);
        mongoMetrics.setComponents(20);
        mongoMetrics.setPolicyViolationsTotal(30);
        mongoMetrics.setPolicyViolationsWarn(40);
        mongoMetrics.setPolicyViolationsFail(50);

        assertNotNull(mongoMetrics);
        assertEquals(1, mongoMetrics.getCritical());
        assertEquals(2, mongoMetrics.getHigh());
        assertEquals(3, mongoMetrics.getMedium());
        assertEquals(4, mongoMetrics.getLow());
        assertEquals(10, mongoMetrics.getVulnerabilities());
        assertEquals(20, mongoMetrics.getComponents());
        assertEquals(30, mongoMetrics.getPolicyViolationsTotal());
        assertEquals(40, mongoMetrics.getPolicyViolationsWarn());
        assertEquals(50, mongoMetrics.getPolicyViolationsFail());
        assertEquals("critical:1 high:2 medium:3 low:4 vulnerabilities:10 policyViolationsTotal:30 policyViolationsWarn:40 policyViolationsFail:50", mongoMetrics.toString());
    }

    @Test
    void shouldUseZeroValuesByDefault() {
        MongoMetricsInfo mongoMetrics = new MongoMetricsInfo();

        assertNotNull(mongoMetrics);
        assertEquals(0, mongoMetrics.getCritical());
        assertEquals(0, mongoMetrics.getHigh());
        assertEquals(0, mongoMetrics.getMedium());
        assertEquals(0, mongoMetrics.getLow());
        assertEquals(0, mongoMetrics.getVulnerabilities());
        assertEquals(0, mongoMetrics.getPolicyViolationsTotal());
        assertEquals(0, mongoMetrics.getPolicyViolationsWarn());
        assertEquals(0, mongoMetrics.getPolicyViolationsFail());
        assertEquals("critical:0 high:0 medium:0 low:0 vulnerabilities:0 policyViolationsTotal:0 policyViolationsWarn:0 policyViolationsFail:0", mongoMetrics.toString());
    }
}
