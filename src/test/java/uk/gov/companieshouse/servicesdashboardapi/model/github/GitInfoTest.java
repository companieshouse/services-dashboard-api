package uk.gov.companieshouse.servicesdashboardapi.model.github;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitInfoTest {

    @Test
    void shouldSetAndGetBasicFields() {
        GitInfo gitInfo = new GitInfo();

        gitInfo.setRepo("https://github.com/companieshouse/services-dashboard-api");
        gitInfo.setLang("Java");
        gitInfo.setOwner("team-photon");
        gitInfo.setServiceArea("Common Components");

        assertEquals("https://github.com/companieshouse/services-dashboard-api", gitInfo.getRepo());
        assertEquals("Java", gitInfo.getLang());
        assertEquals("team-photon", gitInfo.getOwner());
        assertEquals("Common Components", gitInfo.getServiceArea());
        assertEquals("{r:https://github.com/companieshouse/services-dashboard-api l:Java o:team-photon sA:Common Components [R:unknown]}", gitInfo.toString());
    }

    @Test
    void shouldSetAndGetReleases() {
        GitInfo gitInfo = new GitInfo();

        GitReleaseInfo release = new GitReleaseInfo();
        release.setVersion("1.0.0");
        release.setDate("2026-01-01");

        List<GitReleaseInfo> releases = new ArrayList<>();
        releases.add(release);

        gitInfo.setReleases(releases);

        assertNotNull(gitInfo.getReleases());
        assertEquals(1, gitInfo.getReleases().size());
        assertEquals("1.0.0", gitInfo.getReleases().get(0).getVersion());
        assertEquals("2026-01-01", gitInfo.getReleases().get(0).getDate());
        assertEquals("{r:null l:null o:null sA:null [R:[{v:1.0.0,d:2026-01-01}]]}", gitInfo.toString());
    }

    @Test
    void shouldSetEmptyReleasesList() {
        GitInfo gitInfo = new GitInfo();

        gitInfo.setReleases(new ArrayList<>());

        assertNotNull(gitInfo.getReleases());
        assertTrue(gitInfo.getReleases().isEmpty());
    }
}
