package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackMetricsInfo;

public class VersionInfo {

   private String version;
   private String uuid;
   private long lastBomImport;
   private DepTrackMetricsInfo DepTrackMetrics;
   private String runtime;

   // Getters and setters
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

   public DepTrackMetricsInfo getMetrics() {
    return DepTrackMetrics;
   }

  public void setMetrics(DepTrackMetricsInfo metrics) {
    this.DepTrackMetrics = metrics;
  }

   public String getRuntime() {
      return runtime;
   }

   public void setRuntime(String runtime) {
      this.runtime = runtime;
   }

  @Override
  public String toString() {
      return String.format("{v:%s,u:%s,l:%s,m:%s,r:%s}", version, uuid, lastBomImport, DepTrackMetrics, runtime);
  }
}
