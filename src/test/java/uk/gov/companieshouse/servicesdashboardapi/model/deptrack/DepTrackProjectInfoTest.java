package uk.gov.companieshouse.servicesdashboardapi.model.deptrack;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DepTrackProjectInfoTest {

    @Test
    void shouldParseProjectJsonIntoDepTrackProjectInfo() {
        String json = """
                {
                    "name": "disqualified-officers-delta-consumer",
                    "version": "0.8.3",
                    "classifier": "APPLICATION",
                    "uuid": "0ee60287-158e-49d8-b783-946e92ac5d8d",
                    "tags": [
                    {
                        "name": "lang:java"
                    },
                    {
                        "name": "runtime:21.0.1 java-21-amazon-corretto.x86_64 spring-core:6.1.10 spring-boot-starter:3.3.1"
                    }
                    ],
                    "lastBomImport": 1724366968904,
                    "lastBomImportFormat": "CycloneDX 1.5",
                    "lastInheritedRiskScore": 319.0,
                    "active": true,
                    "metrics": {
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
                }
                """;

        JsonMapper jsonMapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        DepTrackProjectInfo projectInfo = jsonMapper.readValue(json, DepTrackProjectInfo.class);

        assertNotNull(projectInfo);
        assertEquals("disqualified-officers-delta-consumer", projectInfo.getName());
        assertEquals("0.8.3", projectInfo.getVersion());
        assertEquals("0ee60287-158e-49d8-b783-946e92ac5d8d", projectInfo.getUuid());
        assertEquals(1724366968904L, projectInfo.getLastBomImport());

        assertNotNull(projectInfo.getTags());
        assertEquals(2, projectInfo.getTags().size());
        assertEquals("lang:java", projectInfo.getTags().getFirst().getName());

        assertNotNull(projectInfo.getMetrics());
        assertEquals(8, projectInfo.getMetrics().getCritical());
        assertEquals(26, projectInfo.getMetrics().getHigh());
        assertEquals(23, projectInfo.getMetrics().getMedium());
        assertEquals(5, projectInfo.getMetrics().getLow());
        assertEquals(69, projectInfo.getMetrics().getVulnerabilities());
        assertEquals(1, projectInfo.getMetrics().getPolicyViolationsFail());
        assertEquals(2, projectInfo.getMetrics().getPolicyViolationsWarn());
        assertEquals(3, projectInfo.getMetrics().getPolicyViolationsTotal());
    }
}
