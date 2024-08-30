package uk.gov.companieshouse.servicesdashboardapi.model.sonar;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SonarComponent {

    @JsonProperty("key")
    private String key;

    @JsonProperty("name")
    private String name;

    @JsonProperty("measures")
    private List<SonarMeasureInfo> measures;

    // Getters and Setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SonarMeasureInfo> getMeasures() {
        return measures;
    }

    public void setMeasures(List<SonarMeasureInfo> measures) {
        this.measures = measures;
    }

   // Method to transform measures list to a map
   public Map<String, Integer> getMeasuresAsMap() {
      if (measures == null) {
          return new HashMap<>();
      }
      return measures.stream().collect(Collectors.toMap(
         SonarMeasureInfo::getMetric,                   // Key  mapper: metric name
         measure -> measure.getValue() != null ?        // Value mapper: converting Float to Integer (rounded)
                    Math.round(measure.getValue()) : 0
      ));
   }

    @Override
    public String toString() {
        return String.format("key=%s, name=%s, measure:%s", key, name, measures);
    }
}
