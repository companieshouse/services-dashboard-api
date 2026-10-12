package uk.gov.companieshouse.servicesdashboardapi.model.dao;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MongoGitInfoTest {
    @Test
    void shouldCreateMongoGitInfoWithValues() {
        MongoGitInfo mongoGitInfo = new MongoGitInfo();
        MongoGitReleaseInfo releaseInfo = new MongoGitReleaseInfo();
        releaseInfo.setVersion("1.0.0");

        List<MongoGitReleaseInfo> releases = List.of(releaseInfo);

        mongoGitInfo.setRepo("repo");
        mongoGitInfo.setLang("lang");
        mongoGitInfo.setOwner("owner");
        mongoGitInfo.setServiceArea("serviceArea");
        mongoGitInfo.setReleases(releases);

        assertNotNull(mongoGitInfo);
        assertEquals("repo", mongoGitInfo.getRepo());
        assertEquals("lang", mongoGitInfo.getLang());
        assertEquals("owner", mongoGitInfo.getOwner());
        assertEquals("serviceArea", mongoGitInfo.getServiceArea());
        assertEquals(releases, mongoGitInfo.getReleases());
        assertEquals("{r:repo, o:owner, sa:serviceArea, [l:[{v:1.0.0,d:null}]]}", mongoGitInfo.toString());
    }
}
