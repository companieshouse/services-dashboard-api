package uk.gov.companieshouse.servicesdashboardapi.model.merge;

import uk.gov.companieshouse.servicesdashboardapi.model.endoflife.EndOfLifeInfo;

import java.util.List;
import java.util.Map;

public class ConfigInfo {

    private Map<String, List<EndOfLifeInfo>> endol;

    // Getters and setters
    public Map<String, List<EndOfLifeInfo>> getEndol() {
        return endol;
    }

    public void setEndol(Map<String, List<EndOfLifeInfo>> endol) {
        this.endol = endol;
    }

    @Override
    public String toString() {
        return String.format("{endol:{%s},", endol);
    }
}
