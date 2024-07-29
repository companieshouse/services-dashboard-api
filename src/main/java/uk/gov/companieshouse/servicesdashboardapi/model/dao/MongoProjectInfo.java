package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MongoProjectInfo {
   @Id
   private String id;

   @Field("name")
   private String name;

   @Field("versions")
   private List<MongoVersionInfo> versions;

   @Field("sonarMetrics")
   private Map<String, Integer> sonarMetrics;

   // Getters and Setters
   public String getName() {
       return name;
   }

   public void setName(String name) {
       this.name = name;
   }

   public List<MongoVersionInfo> getVersions() {
    return versions;
   }

   public void setVersions(List<MongoVersionInfo> versions) {
    this.versions = versions;
   }

   public Map<String, Integer> getSonarMetrics() {
    return sonarMetrics;
}

public void setSonarMetrics(Map<String, Integer> sonarMetrics) {
   this.sonarMetrics = sonarMetrics;
}

  @Override
  public String toString() {
      return String.format("{n:%s,[v:%s],%s}", name, versions, sonarMetrics);
  }
}
