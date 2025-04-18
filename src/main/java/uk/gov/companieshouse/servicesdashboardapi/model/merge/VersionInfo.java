package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackMetricsInfo;

public class VersionInfo {

   private String version;
   private String uuid;
   private long lastBomImport;
   private DepTrackMetricsInfo depTrackMetrics;
   private String lang;
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

   public DepTrackMetricsInfo getDepTrackMetrics() {
    return depTrackMetrics;
   }

   public void setDepTrackMetrics(DepTrackMetricsInfo metrics) {
      this.depTrackMetrics = metrics;
   }

   public String getLang() {
      return lang;
   }

   public void setLang(String lang) {
      this.lang = lang;
   }

   public String getRuntime() {
      return runtime;
   }

   public void setRuntime(String runtime) {
      this.runtime = runtime;
   }

   @Override
   public String toString() {
         return String.format("{v:%s,u:%s,l:%s,m:%s,l:%s,r:%s}", version, uuid, lastBomImport, depTrackMetrics, lang, runtime);
   }
}
