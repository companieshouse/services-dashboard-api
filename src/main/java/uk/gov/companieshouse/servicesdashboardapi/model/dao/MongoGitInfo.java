package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;

public class MongoGitInfo {

   @Field("repo")
   private String repo;

   @Field("lang")
   private String lang;

   @Field("lastRelease")
   private MongoGitLastReleaseInfo lastRelease;

   // Getters and Setters
   public String getRepo() {
       return repo;
   }

   public void setRepo(String repo) {
       this.repo = repo;
   }

   public String getLang() {
      return lang;
   }

   public void setLang(String lang) {
         this.lang = lang;
   }

   public MongoGitLastReleaseInfo getLastRelease() {
    return lastRelease;
   }

   public void setLastRelease(MongoGitLastReleaseInfo lastRelease) {
    this.lastRelease = lastRelease;
   }

  @Override
  public String toString() {
      return String.format("{r:%s,[l:%s]}", repo, lastRelease);
  }
}
