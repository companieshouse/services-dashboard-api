package uk.gov.companieshouse.servicesdashboardapi.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

public abstract class BaseMetricsInfo {
    @JsonProperty("critical")
    @Field("critical")
    protected int critical;

    @JsonProperty("high")
    @Field("high")
    protected int high;

    @JsonProperty("medium")
    @Field("medium")
    protected int medium;

    @JsonProperty("low")
    @Field("low")
    protected int low;

    @JsonProperty("vulnerabilities")
    @Field("vulnerabilities")
    protected int vulnerabilities;

    @JsonProperty("components")
    @Field("components")
    protected int components;

    @JsonProperty("policyViolationsTotal")
    @Field("policyViolationsTotal")
    protected int policyViolationsTotal;

    @JsonProperty("policyViolationsWarn")
    @Field("policyViolationsWarn")
    protected int policyViolationsWarn;

    @JsonProperty("policyViolationsFail")
    @Field("policyViolationsFail")
    protected int policyViolationsFail;

    // Getters and Setters
    public int getCritical() {
        return critical;
    }

    public void setCritical(int critical) {
        this.critical = critical;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(int vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    public int getComponents() {
        return components;
    }

    public void setComponents(int components) {
        this.components = components;
    }

    public int getPolicyViolationsTotal() {
        return policyViolationsTotal;
    }

    public void setPolicyViolationsTotal(int policyViolationsTotal) {
        this.policyViolationsTotal = policyViolationsTotal;
    }

    public int getPolicyViolationsWarn() {
        return policyViolationsWarn;
    }

    public void setPolicyViolationsWarn(int policyViolationsWarn) {
        this.policyViolationsWarn = policyViolationsWarn;
    }

    public int getPolicyViolationsFail() {
        return policyViolationsFail;
    }

    public void setPolicyViolationsFail(int policyViolationsFail) {
        this.policyViolationsFail = policyViolationsFail;
    }
}
