package uk.gov.companieshouse.servicesdashboardapi.model.deptrack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DepTrackTag {

   @JsonProperty("name")
   private String name;

   // Getters and Setters
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
       return String.format("{n:%s}", name);
   }
}


