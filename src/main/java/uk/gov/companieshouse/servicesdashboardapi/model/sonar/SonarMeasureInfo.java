package uk.gov.companieshouse.servicesdashboardapi.model.sonar;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SonarMeasureInfo {

   @JsonProperty("metric")
   private String metric;

   @JsonProperty("value")
   private Float value;

   // Getters and Setters

   public String getMetric() {
       return metric;
   }

   public void setMetric(String metric) {
       this.metric = metric;
   }

   public Float getValue() {
       return value;
   }

   public void setValue(Float value) {
       this.value = value;
   }

   @Override
   public String toString() {
       return String.format("%s:%s", metric, value);
   }
}


