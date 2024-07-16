package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;


public class MongoMetricsInfo {

   @Field("critical")
   private int critical;

   @Field("high")
   private int high;

   @Field("medium")
   private int medium;

   @Field("low")
   private int low;

   @Field("vulnerabilities")
   private int vulnerabilities;

   @Field("components")
   private int components;

   // Getters and Setters
   public int getCritical() {
      return critical;
   }

   public void setCritical(int critical) {
      this.critical = critical;
   }

   public int getHigh() {
      return high;
   }

   public void setHigh(int high) {
      this.high = high;
   }

   public int getMedium() {
      return medium;
   }

   public void setMedium(int medium) {
      this.medium = medium;
   }

   public int getLow() {
      return low;
   }

   public void setLow(int low) {
      this.low = low;
   }

   public int getVulnerabilities() {
      return vulnerabilities;
   }

   public void setVulnerabilities(int vulnerabilities) {
      this.vulnerabilities = vulnerabilities;
   }

   public int getComponents() {
      return components;
   }

   public void setComponents(int components) {
      this.components = components;
   }

   @Override
   public String toString() {
       return "Metrics{" +
              "critical='" + critical + '\'' +
              ", high='" + high + '\'' +
              ", medium='" + medium + '\'' +
              ", low='" + low + '\'' +
              ", vulnerabilities='" + vulnerabilities + '\'' +
              ", components='" + components + '\'' +
              '}';

   }
}

   
