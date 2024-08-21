package uk.gov.companieshouse.servicesdashboardapi.model.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitCustomProperty {

   @JsonProperty("property_name")
   private String propertyName;

   private String value;

   // Getters and Setters
   public String getPropertyName() {
      return propertyName;
   }

   public void setPropertyName(String propertyName) {
      this.propertyName = propertyName;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   @Override
   public String toString() {
       return String.format("{p:%s,v:%s}", propertyName, value);
   }
}


