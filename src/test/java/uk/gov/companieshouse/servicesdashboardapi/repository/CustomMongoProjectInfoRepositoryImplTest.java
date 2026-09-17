package uk.gov.companieshouse.servicesdashboardapi.repository;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import uk.gov.companieshouse.servicesdashboardapi.config.MongoConfig;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoGitInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoVersionInfo;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomMongoProjectInfoRepositoryImplTest {

    private static final String COLLECTION = "projects_collection";

    private MongoTemplate mongoTemplate;
    private CustomMongoProjectInfoRepositoryImpl repository;

    @SuppressWarnings("SameParameterValue")
    private static MongoProjectInfo projectInfo(String name,
                                                List<MongoVersionInfo> versions,
                                                String sonarKey,
                                                Map<String, Integer> sonarMetrics,
                                                MongoGitInfo gitInfo) {
        MongoProjectInfo info = new MongoProjectInfo();
        info.setName(name);
        info.setVersions(versions);
        info.setSonarKey(sonarKey);
        info.setSonarMetrics(sonarMetrics);
        info.setGitInfo(gitInfo);
        return info;
    }

    private static MongoVersionInfo version(String version, String uuid) {
        MongoVersionInfo info = new MongoVersionInfo();
        info.setVersion(version);
        info.setUuid(uuid);
        return info;
    }

    private static MongoGitInfo gitInfo(String owner) {
        MongoGitInfo info = new MongoGitInfo();
        info.setOwner(owner);
        return info;
    }

    private static String uuidForVersion(List<MongoVersionInfo> versions, String version) {
        return versions.stream()
                .filter(v -> version.equals(v.getVersion()))
                .findFirst()
                .map(MongoVersionInfo::getUuid)
                .orElse(null);
    }

    @BeforeEach
    void setUp() {
        mongoTemplate = mock(MongoTemplate.class);
        MongoConfig mongoConfig = mock(MongoConfig.class);
        when(mongoConfig.getCollectionNameProj()).thenReturn(COLLECTION);
        repository = new CustomMongoProjectInfoRepositoryImpl(mongoTemplate, mongoConfig);
    }

    @Test
    void savesProjectByInsertWhenNameDoesNotExist() {
        MongoProjectInfo info = projectInfo("service-a", List.of(version("1.0.0", "uuid-1")), "sonar-a", Map.of("bugs", 2), gitInfo("team-a"));
        when(mongoTemplate.exists(any(Query.class), eq(MongoProjectInfo.class), eq(COLLECTION))).thenReturn(false);

        repository.saveProjectInfos(List.of(info));

        verify(mongoTemplate).insert(info, COLLECTION);
        verify(mongoTemplate, never()).updateFirst(any(Query.class), any(Update.class), eq(MongoProjectInfo.class), eq(COLLECTION));
    }

    @Test
    void updatesExistingProjectAndMergesVersionsByVersion() {
        MongoVersionInfo existingV1 = version("1.0.0", "uuid-old-1");
        MongoVersionInfo existingV2 = version("2.0.0", "uuid-old-2");
        MongoProjectInfo existing = projectInfo("service-a", List.of(existingV1, existingV2), "old-sonar", Map.of("bugs", 5), gitInfo("old-owner"));

        MongoVersionInfo newV1 = version("1.0.0", "uuid-new-1");
        MongoVersionInfo newV3 = version("3.0.0", "uuid-new-3");
        MongoGitInfo newGitInfo = gitInfo("new-owner");
        Map<String, Integer> newMetrics = Map.of("bugs", 1);
        MongoProjectInfo incoming = projectInfo("service-a", List.of(newV1, newV3), "new-sonar", newMetrics, newGitInfo);

        when(mongoTemplate.exists(any(Query.class), eq(MongoProjectInfo.class), eq(COLLECTION))).thenReturn(true);
        when(mongoTemplate.findOne(any(Query.class), eq(MongoProjectInfo.class), eq(COLLECTION))).thenReturn(existing);

        repository.saveProjectInfos(List.of(incoming));

        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        verify(mongoTemplate).updateFirst(any(Query.class), updateCaptor.capture(), eq(MongoProjectInfo.class), eq(COLLECTION));

        Document setDoc = (Document) updateCaptor.getValue().getUpdateObject().get("$set");
        @SuppressWarnings("unchecked")
        List<MongoVersionInfo> mergedVersions = (List<MongoVersionInfo>) setDoc.get("versions");

        assertEquals(3, mergedVersions.size());
        assertEquals("uuid-new-1", uuidForVersion(mergedVersions, "1.0.0"));
        assertEquals("uuid-old-2", uuidForVersion(mergedVersions, "2.0.0"));
        assertEquals("uuid-new-3", uuidForVersion(mergedVersions, "3.0.0"));
        assertEquals("new-sonar", setDoc.get("sonarKey"));
        assertEquals(newMetrics, setDoc.get("sonarMetrics"));
        assertSame(newGitInfo, setDoc.get("gitInfo"));
    }

    @Test
    void skipsUpdateWhenExistingDocumentCannotBeLoaded() {
        MongoProjectInfo incoming = projectInfo("service-a", List.of(version("1.0.0", "uuid-new")), "new-sonar", Map.of(), gitInfo("new-owner"));

        when(mongoTemplate.exists(any(Query.class), eq(MongoProjectInfo.class), eq(COLLECTION))).thenReturn(true);
        when(mongoTemplate.findOne(any(Query.class), eq(MongoProjectInfo.class), eq(COLLECTION))).thenReturn(null);

        repository.saveProjectInfos(List.of(incoming));

        verify(mongoTemplate, never()).updateFirst(any(Query.class), any(Update.class), eq(MongoProjectInfo.class), eq(COLLECTION));
        verify(mongoTemplate, never()).insert(any(MongoProjectInfo.class), eq(COLLECTION));
    }

    @Test
    void existsByNameReturnsTrueWhenTemplateFindsMatch() {
        when(mongoTemplate.exists(any(Query.class), eq(MongoProjectInfo.class), eq(COLLECTION))).thenReturn(true);

        assertTrue(repository.existsByName("service-a"));
    }

    @Test
    void existsByUuidReturnsFalseWhenTemplateFindsNoMatch() {
        when(mongoTemplate.exists(any(Query.class), eq(MongoProjectInfo.class), eq(COLLECTION))).thenReturn(false);

        assertFalse(repository.existsByUuid("service-a", "uuid-1"));
    }

    @Test
    void findByNameReturnsProjectWhenPresent() {
        MongoProjectInfo existing = projectInfo("service-a", List.of(version("1.0.0", "uuid-1")), "sonar-a", Map.of("bugs", 2), gitInfo("owner-a"));
        when(mongoTemplate.findOne(any(Query.class), eq(MongoProjectInfo.class))).thenReturn(existing);

        assertTrue(repository.findByName("service-a").isPresent());
        assertSame(existing, repository.findByName("service-a").orElseThrow());
    }

    @Test
    void findByNameReturnsEmptyWhenNotPresent() {
        when(mongoTemplate.findOne(any(Query.class), eq(MongoProjectInfo.class))).thenReturn(null);

        assertTrue(repository.findByName("service-a").isEmpty());
    }

}
