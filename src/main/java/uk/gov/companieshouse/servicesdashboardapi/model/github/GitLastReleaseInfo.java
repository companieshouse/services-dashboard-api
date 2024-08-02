package uk.gov.companieshouse.servicesdashboardapi.model.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitLastReleaseInfo {

   @JsonProperty("tag_name")
   private String version;

   @JsonProperty("published_at")
   private String date;

   // Getters and Setters
      public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   @Override
   public String toString() {
       return String.format("{v:%s,d:%s}", version, date);
   }

}


