package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

   @Field("sonarKey")
   private String sonarKey;

   @Field("sonarMetrics")
   private Map<String, Integer> sonarMetrics;

   @Field("gitInfo")
   private MongoGitInfo gitInfo;

   @Field("ecs")
   private Map<String, Set<String>> ecs;

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

   public String getSonarKey() {
      return sonarKey;
   }

   public void setSonarKey(String sonarKey) {
      this.sonarKey = sonarKey;
   }

   public Map<String, Integer> getSonarMetrics() {
    return sonarMetrics;
   }

   public void setSonarMetrics(Map<String, Integer> sonarMetrics) {
      this.sonarMetrics = sonarMetrics;
   }

   public MongoGitInfo getGitInfo() {
      return gitInfo;
   }

   public void setGitInfo(MongoGitInfo gitInfo) {
      this.gitInfo = gitInfo;
   }

   public Map<String, Set<String>> getEcs() {
      return ecs;
   }

   public void setEcs(Map<String, Set<String>> ecs) {
         this.ecs = ecs;
   }

   @Override
   public String toString() {
      return String.format("{n:%s,[v:%s],%s,%s} [%s]", name, versions, sonarMetrics, gitInfo, sonarKey);
   }
}
