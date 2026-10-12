package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class MongoVersionInfo {

    @Field("version")
    private String version;

    @Field("uuid")
    private String uuid;

    @Field("lastBomImport")
    private Instant lastBomImport;

    @Field("metrics")
    private MongoMetricsInfo metrics;

    @Field("lang")
    private String lang;

    @Field("runtime")
    private String runtime;

    // Getters and Setters

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Instant getLastBomImport() {
        return lastBomImport;
    }

    public void setLastBomImport(Instant lastBomImport) {
        this.lastBomImport = lastBomImport;
    }

    public MongoMetricsInfo getMetrics() {
        return metrics;
    }

    public void setMetrics(MongoMetricsInfo metrics) {
        this.metrics = metrics;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    @Override
    public String toString() {
        return String.format("{v:%s,u:%s,l:%s,%s,%s,%s}", version, uuid, lastBomImport, metrics, lang, runtime);
    }
}


