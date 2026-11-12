package uk.gov.companieshouse.servicesdashboardapi.model.sonar;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the response from SonarQube API for project information.
 * <p>
 * Here how a response to
 *  /api/measures/component?component=uk.gov.companieshouse:api-sdk-java&metricKeys=software_quality_security_issues,software_quality_reliability_issues,software_quality_maintainability_issues,coverage
 *  looks like:
 * <p>
 * {@snippet lang = json:
 * {
 *   "component": {
 *     "key": "uk.gov.companieshouse:api-sdk-java",
 *     "name": "api-sdk-java",
 *     "description": "Contains common configuration and dependencies",
 *     "qualifier": "TRK",
 *     "measures": [
 *       {
 *         "metric": "software_quality_security_issues",
 *         "value": "0",
 *         "bestValue": true
 *       },
 *       {
 *         "metric": "bugs",
 *         "value": "1",
 *         "bestValue": false
 *       },
 *       {
 *         "metric": "coverage",
 *         "value": "92.4",
 *         "bestValue": false
 *       },
 *       {
 *         "metric": "software_quality_maintainability_issues",
 *         "value": "129",
 *         "bestValue": false
 *       }
 *     ]
 *   }
 * }
 *}
 **/
public class SonarProjectInfo {

    @JsonProperty("component")
    private SonarComponent component;

    public SonarComponent getComponent() {
        return component;
    }

    public void setComponent(SonarComponent component) {
        this.component = component;
    }

    @Override
    public String toString() {
        return String.format("component:%s", component);
    }

}
