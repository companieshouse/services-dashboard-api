package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class MongoGitInfo {

   @Field("repo")
   private String repo;

   @Field("lang")
   private String lang;

   @Field("releases")
   private List<MongoGitReleaseInfo> releases;

   @Field("owner")
   private String owner;

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

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public void setLang(String lang) {
         this.lang = lang;
   }

   public List<MongoGitReleaseInfo> getReleases() {
      return releases;
   }

   public void setReleases(List<MongoGitReleaseInfo> releases) {
      this.releases = releases;
   }


  @Override
  public String toString() {
      return String.format("{r:%s, o:%s, [l:%s]}", repo, owner, releases.toString());
  }
}
