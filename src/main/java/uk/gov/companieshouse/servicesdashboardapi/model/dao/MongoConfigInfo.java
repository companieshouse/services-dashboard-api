package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MongoConfigInfo {
   @Id
   private String id;

   @Field("endol")
   private Map<String, List<MongoEndoflifeInfo>> endol;

   // Getters and Setters
   public void setId(String id) {
      this.id = id;
   }

   public Map<String, List<MongoEndoflifeInfo>> getEndol() {
      return endol;
   }

   public void setEndol(Map<String, List<MongoEndoflifeInfo>> endol) {
      this.endol = endol;
   }

   @Override
   public String toString() {
      return String.format("{endol:{%s},", endol);
   }
}
