package uk.gov.companieshouse.servicesdashboardapi.model.sonar;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SonarMeasureInfo {

   @JsonProperty("metric")
   private String metric;

   @JsonProperty("value")
   private String value;

   // Getters and Setters

   public String getMetric() {
       return metric;
   }

   public void setMetric(String metric) {
       this.metric = metric;
   }

   public String getValue() {
       return value;
   }

   public void setValue(String value) {
       this.value = value;
   }

   @Override
   public String toString() {
       return String.format("%s:%s", metric, value);
   }
}


