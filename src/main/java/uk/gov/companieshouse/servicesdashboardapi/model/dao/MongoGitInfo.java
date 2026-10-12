package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class MongoGitInfo {

    @Field("repo")
    private String repo;

    @Field("lang")
    private String lang;

    @Field("releases")
    private List<MongoGitReleaseInfo> releases;

    @Field("owner")
    private String owner;

    @Field("serviceArea")
    private String serviceArea;

    // Getters and Setters
    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<MongoGitReleaseInfo> getReleases() {
        return releases;
    }

    public void setReleases(List<MongoGitReleaseInfo> releases) {
        this.releases = releases;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }


    @Override
    public String toString() {
        return String.format("{r:%s, o:%s, sa:%s, [l:%s]}", repo, owner, serviceArea, releases.toString());
    }
}
