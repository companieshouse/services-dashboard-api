package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

public class MongoVersionInfo {

   @Field("version")
   private String version;

   @Field("lastBomImport")
   private Date lastBomImport;

   @Field("metrics")
   private MongoMetricsInfo metrics;

   // Getters and Setters

   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
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

   @Override
   public String toString() {
       return String.format("{v:%s,l:%s,%s}", version, lastBomImport, metrics);
   }
}


