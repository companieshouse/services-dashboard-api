package uk.gov.companieshouse.servicesdashboardapi.model.github;

import uk.gov.companieshouse.servicesdashboardapi.utils.GitUtils;

import java.util.ArrayList;
import java.util.List;

public class GitInfo {

    private String repo;
    private String lang;
    private String owner;
    private String serviceArea;
    private List<GitReleaseInfo> releases;

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

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public List<GitReleaseInfo> getReleases() {
        return releases;
    }

    public void setReleases(List<GitReleaseInfo> gitReleases) {
        this.releases = gitReleases.isEmpty() ? new ArrayList<>() : GitUtils.filterReleases(gitReleases);
    }

    @Override
    public String toString() {
        return String.format("{r:%s l:%s o:%s sA:%s [R:%s]}", repo, lang, owner, serviceArea, (releases == null || releases.isEmpty()) ? "unknown" : releases.toString());
    }
}

