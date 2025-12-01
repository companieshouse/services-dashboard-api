package uk.gov.companieshouse.servicesdashboardapi.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import uk.gov.companieshouse.servicesdashboardapi.config.MongoConfig;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoVersionInfo;

@Repository
public class CustomMongoProjectInfoRepositoryImpl implements CustomMongoProjectInfoRepository {

   private final MongoTemplate mongoTemplate;
   private final String collectionName;

   public CustomMongoProjectInfoRepositoryImpl(MongoTemplate mongoTemplate,
                                               MongoConfig mongoConfig) {
      this.mongoTemplate = mongoTemplate;
      this.collectionName = mongoConfig.getCollectionNameProj();
   }

   @Override
   public void saveProjectInfos(List<MongoProjectInfo> mongoProjectInfoList) {
      for (MongoProjectInfo info : mongoProjectInfoList) {
            if (existsByName(info.getName())) {
               updateEntry(info);
            } else {
               mongoTemplate.insert(info, collectionName);
            }
      }
   }

   @Override
   public boolean existsByName(String name) {
      Query query = new Query();
      query.addCriteria(Criteria.where("name").is(name));
      return mongoTemplate.exists(query, MongoProjectInfo.class, collectionName);
   }

   @Override
   public boolean existsByUuid(String name, String uuid) {
      Query query = new Query();
      query.addCriteria(Criteria.where("name").is(name)
                               .and("versions.uuid").is(uuid));
      return mongoTemplate.exists(query, MongoProjectInfo.class, collectionName);
   }

   @Override
   public Optional<MongoProjectInfo> findByName(String name) {
      Query query = new Query();
      query.addCriteria(Criteria.where("name").is(name));
      MongoProjectInfo result = mongoTemplate.findOne(query, MongoProjectInfo.class);
      // Return the result wrapped in an Optional
      return Optional.ofNullable(result);
   }

   private void updateEntry(MongoProjectInfo newInfo) {
      Query query = new Query();
      query.addCriteria(Criteria.where("name").is(newInfo.getName()));

      MongoProjectInfo existingInfo = mongoTemplate.findOne(query, MongoProjectInfo.class, collectionName);

      if (existingInfo != null) {
         List<MongoVersionInfo> existingVersions = existingInfo.getVersions();
         List<MongoVersionInfo> newVersions = newInfo.getVersions();

         // A map to merge versions by version key
         Map<String, MongoVersionInfo> versionMap = new HashMap<>();
         if (existingVersions != null) {
            for (MongoVersionInfo v : existingVersions) {
               versionMap.put(v.getVersion(), v);
            }
         }
         if (newVersions != null) {
            for (MongoVersionInfo v : newVersions) {
               versionMap.put(v.getVersion(), v); // override or add
            }
         }

         List<MongoVersionInfo> mergedVersions = new ArrayList<>(versionMap.values());

         // Update the document with the merged versions
         Update update = new Update();
         update.set("versions", mergedVersions);

         // Update the other fields
         update.set("sonarKey", newInfo.getSonarKey());
         update.set("sonarMetrics", newInfo.getSonarMetrics());
         update.set("gitInfo", newInfo.getGitInfo());

         mongoTemplate.updateFirst(query, update, MongoProjectInfo.class, collectionName);
      }
   }
}
