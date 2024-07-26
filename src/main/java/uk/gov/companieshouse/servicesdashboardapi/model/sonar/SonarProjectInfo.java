package uk.gov.companieshouse.servicesdashboardapi.model.sonar;

import com.fasterxml.jackson.annotation.JsonProperty;
/*
 * Here how a response to
 * /api/measures/component?component=uk.gov.companieshouse:api-sdk-java&metricKeys=vulnerabilities,bugs,code_smells,coverage
 * looks like:
 * {
  "component": {
    "key": "uk.gov.companieshouse:api-sdk-java",
    "name": "api-sdk-java",
    "description": "Contains common configuration and dependencies",
    "qualifier": "TRK",
    "measures": [
      {
        "metric": "vulnerabilities",
        "value": "0",
        "bestValue": true
      },
      {
        "metric": "bugs",
        "value": "1",
        "bestValue": false
      },
      {
        "metric": "coverage",
        "value": "92.4",
        "bestValue": false
      },
      {
        "metric": "code_smells",
        "value": "129",
        "bestValue": false
      }
    ]
  }
}
 */
public class SonarProjectInfo  {

    @JsonProperty("component")
    private SonarComponent component;

    // Getters and Setters

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
