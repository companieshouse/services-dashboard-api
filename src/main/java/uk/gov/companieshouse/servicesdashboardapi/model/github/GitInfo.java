package uk.gov.companieshouse.servicesdashboardapi.model.github;

public class GitInfo {

   private String repo;
   private String lang;
   private GitLastReleaseInfo lastRelease;

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

   public GitLastReleaseInfo getLastRelease() {
      return lastRelease;
   }

   public void setLastRelease(GitLastReleaseInfo lastRelease) {
      this.lastRelease = lastRelease;
   }

   @Override
   public String toString() {
       return String.format("{r:%s l:%s [R:%s]}", repo, lang, lastRelease);
   }

}


