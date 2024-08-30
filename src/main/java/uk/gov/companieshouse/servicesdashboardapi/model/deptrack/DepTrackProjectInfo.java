package uk.gov.companieshouse.servicesdashboardapi.model.deptrack;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Example of Info:
 *   {
    "name": "disqualified-officers-delta-consumer",
    "version": "0.8.3",
    "classifier": "APPLICATION",
    "uuid": "0ee60287-158e-49d8-b783-946e92ac5d8d",
    "tags": [
      {
        "name": "runtime:21.0.1 java-21-amazon-corretto.x86_64 spring-core:5.3.16 spring-boot-starter:2.6.4"
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
      "policyViolationsFail": 0,
      "policyViolationsWarn": 0,
      "policyViolationsInfo": 0,
      "policyViolationsTotal": 0,
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
]

 */

public class DepTrackProjectInfo  {

   @JsonProperty("name")
   private String name;

   @JsonProperty("version")
   private String version;

   @JsonProperty("uuid")
   private String uuid;

   @JsonProperty("lastBomImport")
   private long lastBomImport;

   @JsonProperty("tags")
   private List<DepTrackTag> tags;

   @JsonProperty("metrics")
   private DepTrackMetricsInfo metrics;

   // Getters and Setters
   public String getName() {
       return name;
   }

   public void setName(String name) {
       this.name = name;
   }

   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getUuid() {
      return uuid;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   public long getLastBomImport() {
      return lastBomImport;
   }

   public void setLastBomImport(long lastBomImport) {
      this.lastBomImport = lastBomImport;
   }
   public List<DepTrackTag> getTags() {
      return tags;
   }

   public void setTags(List<DepTrackTag> tags) {
         this.tags = tags;
   }

   public DepTrackMetricsInfo getMetrics() {
      return metrics;
   }

  public void setMetrics(DepTrackMetricsInfo metrics) {
      this.metrics = metrics;
   }

   @Override
   public String toString() {
      return String.format("DepTrackProjectInfo{n:%s,v:%s,id:%s,imp:%s,tags:[%s],m:%s}",
      name,
      version,
      uuid,
      lastBomImport,
      tags,
      metrics);
   }
}
