package uk.gov.companieshouse.servicesdashboardapi.model.sonar;

import uk.gov.companieshouse.servicesdashboardapi.utils.DeserializerSonarMeasures;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

public class SonarComponent {

    @JsonProperty("key")
    private String key;

    @JsonProperty("name")
    private String name;

    @JsonProperty("measures")
    @JsonDeserialize(using = DeserializerSonarMeasures.class)
    private Map<String, Integer>measures;

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

    public Map<String, Integer> getMeasures() {
        return measures;
    }

    public void setMeasures(Map<String, Integer> measures) {
        this.measures = measures;
    }

    @Override
    public String toString() {
        return String.format("key=%s, name=%s, measure:%s", key, name, measures);
    }
}
