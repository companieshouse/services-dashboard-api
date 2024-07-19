package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// @Document(collection = "projects")
@Document
public class MongoProjectInfo {
   @Id
   private String id;

   @Field("name")
   private String name;

   @Field("version")
   private String version;

   @Field("lastBomImport")
   private Date lastBomImport;
   
   @Field("metrics")
   private MongoMetricsInfo metrics;

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
       return "DepTrackProjectInfo{" +
       "name='" + name + '\'' +
       ", version='" + version + '\'' +
       ", lastBomImport='" + lastBomImport + '\'' +
       ", metrics='" + metrics + '\'' +
              '}';
   }
}
