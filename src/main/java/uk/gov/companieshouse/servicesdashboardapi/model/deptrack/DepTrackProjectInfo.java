package uk.gov.companieshouse.servicesdashboardapi.model.deptrack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DepTrackProjectInfo  {

   @JsonProperty("name")
   private String name;

   @JsonProperty("version")
   private String version;

   @JsonProperty("lastBomImport")
   private long lastBomImport;

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

   public long getLastBomImport() {
      return lastBomImport;
   }

   public void setLastBomImport(long lastBomImport) {
      this.lastBomImport = lastBomImport;
   }
   public DepTrackMetricsInfo getMetrics() {
      return metrics;
   }

  public void setMetrics(DepTrackMetricsInfo metrics) {
      this.metrics = metrics;
   }

   @Override
   public String toString() {
      return String.format("DepTrackProjectInfo{name:%s,version:%s,lastBomImport:%s,metrics:%s}",
      name,
      version,
      lastBomImport,
      metrics);
   }
}
