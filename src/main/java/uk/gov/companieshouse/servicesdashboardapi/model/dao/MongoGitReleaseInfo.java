package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

public class MongoGitReleaseInfo {

    @Field("version")
    private String version;

    @Field("date")
    private LocalDate date;

    // Getters and Setters

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("{v:%s,d:%s}", version, date);
    }
}


