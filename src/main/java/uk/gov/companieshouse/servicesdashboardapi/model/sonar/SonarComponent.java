package uk.gov.companieshouse.servicesdashboardapi.model.sonar;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

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

    @Override
    public String toString() {
        return String.format("key=%s, name=%s, measure:%s", key, name, measures);
    }
}
