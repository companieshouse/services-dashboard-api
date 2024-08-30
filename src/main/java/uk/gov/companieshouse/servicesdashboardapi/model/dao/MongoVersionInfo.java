package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

public class MongoVersionInfo {

   @Field("version")
   private String version;

   @Field("uuid")
   private String uuid;

   @Field("lastBomImport")
   private Date lastBomImport;

   @Field("metrics")
   private MongoMetricsInfo metrics;

   @Field("runtime")
   private String runtime;

   // Getters and Setters

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

   public Date getLastBomImport() {
      return lastBomImport;
   }

   public void setLastBomImport(Date lastBomImport) {
      this.lastBomImport = lastBomImport;
   }
   public MongoMetricsInfo getMetrics() {
      return metrics;
   }

  public void setMetrics(MongoMetricsInfo metrics) {
      this.metrics = metrics;
   }

   public String getRuntime() {
      return runtime;
   }

   public void setRuntime(String runtime) {
      this.runtime = runtime;
   }

   @Override
   public String toString() {
       return String.format("{v:%s,u:%s,l:%s,%s,%s}", version, uuid, lastBomImport, metrics, runtime);
   }
}


