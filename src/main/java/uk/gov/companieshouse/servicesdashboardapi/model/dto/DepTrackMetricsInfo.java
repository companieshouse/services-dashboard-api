package uk.gov.companieshouse.servicesdashboardapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DepTrackMetricsInfo {
   @JsonProperty("critical")
   private int critical;

   @JsonProperty("high")
   private int high;

   @JsonProperty("medium")
   private int medium;

   @JsonProperty("low")
   private int low;

   @JsonProperty("vulnerabilities")
   private int vulnerabilities;

   @JsonProperty("components")
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
       return "DepTrackMetrics{" +
              "critical='" + critical + '\'' +
              ", high='" + high + '\'' +
              ", medium='" + medium + '\'' +
              ", low='" + low + '\'' +
              ", vulnerabilities='" + vulnerabilities + '\'' +
              ", components='" + components + '\'' +
              '}';

   }
}
