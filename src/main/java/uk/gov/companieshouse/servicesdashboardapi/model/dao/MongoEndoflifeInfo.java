package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;

public class MongoEndoflifeInfo {

   @Field("cycle")
   private String cycle;

   @Field("releaseDate")
   private String releaseDate;

   @Field("lts")
   private String lts;

   @Field("eol")
   private String eol;

   @Field("latest")
   private String latest;

   @Field("latestReleaseDate")
   private String latestReleaseDate;

   // Getters and Setters

   public String getCycle() {
      return cycle;
   }
   public void setCycle(String cycle) {
      this.cycle = cycle;
   }

   public String getReleaseDate() {
      return releaseDate;
   }
   public void setReleaseDate(String releaseDate) {
      this.releaseDate = releaseDate;
   }

   public String getLts() {
      return lts;
   }
   public void setLts(String lts) {
      this.lts = lts;
   }

   public String getEol() {
      return eol;
   }
   public void setEol(String eol) {
      this.eol = eol;
   }

   public String getLatest() {
      return latest;
   }
   public void setLatest(String latest) {
      this.latest = latest;
   }

   public String getLatestReleaseDate() {
      return latestReleaseDate;
   }
   public void setLatestReleaseDate(String latestReleaseDate) {
      this.latestReleaseDate = latestReleaseDate;
   }

   @Override
   public String toString() {
      return String.format("cycle=%s, releaseDate=%s, lts:%s, eol=%s, latest=%s, latestReleaseDate:%s",
      cycle,
      releaseDate,
      lts,
      eol,
      latest,
      latestReleaseDate);
   }
}


