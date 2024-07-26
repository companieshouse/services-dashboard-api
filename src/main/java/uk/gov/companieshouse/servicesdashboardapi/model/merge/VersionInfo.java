package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackMetricsInfo;

public class VersionInfo {

   private String version;
   private long lastBomImport;
   private DepTrackMetricsInfo DepTrackMetrics;

   // Getters and setters
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
    return DepTrackMetrics;
   }

  public void setMetrics(DepTrackMetricsInfo metrics) {
    this.DepTrackMetrics = metrics;
  }
  @Override
  public String toString() {
      return String.format("{v:%s,l:%s,%s}", version, lastBomImport, DepTrackMetrics);
  }
}
