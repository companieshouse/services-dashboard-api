package uk.gov.companieshouse.servicesdashboardapi.model.deptrack;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DepTrackMetricsInfoTest {

    private final JsonMapper jsonMapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @Test
    void shouldParseMetricsJsonIntoDepTrackMetricsInfo() {
        String json = """
                {
                    "critical": 8,
                    "high": 26,
                    "medium": 23,
                    "low": 5,
                    "unassigned": 7,
                    "vulnerabilities": 69,
                    "vulnerableComponents": 42,
                    "components": 230,
                    "suppressed": 0,
                    "findingsTotal": 69,
                    "findingsAudited": 0,
                    "findingsUnaudited": 69,
                    "inheritedRiskScore": 319.0,
                    "policyViolationsFail": 1,
                    "policyViolationsWarn": 2,
                    "policyViolationsInfo": 0,
                    "policyViolationsTotal": 3,
                    "policyViolationsAudited": 0,
                    "policyViolationsUnaudited": 0,
                    "policyViolationsSecurityTotal": 0,
                    "policyViolationsSecurityAudited": 0,
                    "policyViolationsSecurityUnaudited": 0,
                    "policyViolationsLicenseTotal": 0,
                    "policyViolationsLicenseAudited": 0,
                    "policyViolationsLicenseUnaudited": 0,
                    "policyViolationsOperationalTotal": 0,
                    "policyViolationsOperationalAudited": 0,
                    "policyViolationsOperationalUnaudited": 0,
                    "firstOccurrence": 1724366983276,
                    "lastOccurrence": 1725015967214
                }
                """;

        DepTrackMetricsInfo metricsInfo = jsonMapper.readValue(json, DepTrackMetricsInfo.class);

        assertNotNull(metricsInfo);
        assertEquals(8, metricsInfo.getCritical());
        assertEquals(26, metricsInfo.getHigh());
        assertEquals(23, metricsInfo.getMedium());
        assertEquals(5, metricsInfo.getLow());
        assertEquals(69, metricsInfo.getVulnerabilities());
        assertEquals(230, metricsInfo.getComponents());
        assertEquals(1, metricsInfo.getPolicyViolationsFail());
        assertEquals(2, metricsInfo.getPolicyViolationsWarn());
        assertEquals(3, metricsInfo.getPolicyViolationsTotal());
    }
}
