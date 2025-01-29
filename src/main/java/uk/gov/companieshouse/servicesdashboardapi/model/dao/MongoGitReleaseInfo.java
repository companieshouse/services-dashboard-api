package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

public class MongoGitReleaseInfo {

   @Field("version")
   private String version;

   @Field("date")
   private Date date;

   // Getters and Setters

   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   @Override
   public String toString() {
       return String.format("{v:%s,d:%s}", version, date);
   }
}


