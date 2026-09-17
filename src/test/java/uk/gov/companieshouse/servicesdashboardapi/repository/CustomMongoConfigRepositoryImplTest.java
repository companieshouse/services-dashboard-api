package uk.gov.companieshouse.servicesdashboardapi.repository;

import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.servicesdashboardapi.config.MongoConfig;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoEndoflifeInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomMongoConfigRepositoryImplTest {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void savesConfigInfoWithoutUpsertWhenExistingDocumentIsUpdated() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        MongoConfig mongoConfig = mock(MongoConfig.class);
        when(mongoConfig.getCollectionNameConf()).thenReturn("config_collection");
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq("config_collection")))
                .thenReturn(UpdateResult.acknowledged(1L, 1L, null));

        CustomMongoConfigRepositoryImpl repository = new CustomMongoConfigRepositoryImpl(mongoTemplate, mongoConfig);
        ReflectionTestUtils.setField(repository, "singletonId", "singleton-config-id");

        MongoConfigInfo configInfo = new MongoConfigInfo();
        Map<String, List<MongoEndoflifeInfo>> endol = Collections.emptyMap();
        configInfo.setEndol(endol);

        repository.saveConfigInfo(configInfo);

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        verify(mongoTemplate).updateFirst(queryCaptor.capture(), updateCaptor.capture(), eq("config_collection"));
        verify(mongoTemplate, never()).upsert(any(Query.class), any(Update.class), eq("config_collection"));

        assertEquals("singleton-config-id", queryCaptor.getValue().getQueryObject().get("_id"));
        Document setDoc = (Document) updateCaptor.getValue().getUpdateObject().get("$set");
        assertEquals(endol, setDoc.get("endol"));
        assertNotNull(setDoc.get("lastScan"));

        String lastScan = setDoc.getString("lastScan");
        assertTrue(lastScan.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"));
        LocalDateTime.parse(lastScan, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Test
    void upsertsConfigInfoWhenNoExistingDocumentWasUpdated() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        MongoConfig mongoConfig = mock(MongoConfig.class);
        when(mongoConfig.getCollectionNameConf()).thenReturn("config_collection");
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq("config_collection")))
                .thenReturn(UpdateResult.acknowledged(0L, 0L, null));

        CustomMongoConfigRepositoryImpl repository = new CustomMongoConfigRepositoryImpl(mongoTemplate, mongoConfig);
        ReflectionTestUtils.setField(repository, "singletonId", "singleton-config-id");

        MongoConfigInfo configInfo = new MongoConfigInfo();
        configInfo.setEndol(Collections.emptyMap());

        repository.saveConfigInfo(configInfo);

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        verify(mongoTemplate).upsert(queryCaptor.capture(), updateCaptor.capture(), eq("config_collection"));

        assertEquals("singleton-config-id", queryCaptor.getValue().getQueryObject().get("_id"));
        Document setDoc = (Document) updateCaptor.getValue().getUpdateObject().get("$set");
        assertNotNull(setDoc.get("lastScan"));
        assertEquals(Collections.emptyMap(), setDoc.get("endol"));
    }

    @Test
    void savesConfigInfoWithNullEndolValue() {
        MongoTemplate mongoTemplate = mock(MongoTemplate.class);
        MongoConfig mongoConfig = mock(MongoConfig.class);
        when(mongoConfig.getCollectionNameConf()).thenReturn("config_collection");
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq("config_collection")))
                .thenReturn(UpdateResult.acknowledged(1L, 1L, null));

        CustomMongoConfigRepositoryImpl repository = new CustomMongoConfigRepositoryImpl(mongoTemplate, mongoConfig);
        ReflectionTestUtils.setField(repository, "singletonId", "singleton-config-id");

        MongoConfigInfo configInfo = new MongoConfigInfo();
        configInfo.setEndol(null);

        repository.saveConfigInfo(configInfo);

        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        verify(mongoTemplate).updateFirst(any(Query.class), updateCaptor.capture(), eq("config_collection"));
        Document setDoc = (Document) updateCaptor.getValue().getUpdateObject().get("$set");
        assertTrue(setDoc.containsKey("endol"));
        assertNull(setDoc.get("endol"));
    }
}
