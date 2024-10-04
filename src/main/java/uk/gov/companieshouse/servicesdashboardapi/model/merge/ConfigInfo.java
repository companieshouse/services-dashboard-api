package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import java.util.List;
import java.util.Map;

import uk.gov.companieshouse.servicesdashboardapi.model.endoflife.EndofLifeInfo;

public class ConfigInfo {

   private Map<String, List<EndofLifeInfo>> endol;

   // Getters and setters
   public Map<String, List<EndofLifeInfo>> getEndol() {
      return endol;
   }

   public void setEndol(Map<String, List<EndofLifeInfo>> endol) {
      this.endol = endol;
   }

   @Override
   public String toString() {
      return String.format("{endol:{%s},", endol);
   }
}
