package uk.gov.companieshouse.servicesdashboardapi.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.stereotype.Repository;

import uk.gov.companieshouse.servicesdashboardapi.config.MongoConfig;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;

@Repository
public class CustomMongoConfigRepositoryImpl implements CustomMongoConfigRepository {

   @Value("${mongo.configObjectId}")
   private String singletonId;

   private final MongoTemplate mongoTemplate;
   private final String collectionName;

   public CustomMongoConfigRepositoryImpl(MongoTemplate mongoTemplate,
                                          MongoConfig mongoConfig) {
      this.mongoTemplate = mongoTemplate;
      this.collectionName = mongoConfig.getCollectionNameConf();
   }

   @Override
   public void saveConfigInfo(MongoConfigInfo configInfo) {
      configInfo.setId(singletonId);
      mongoTemplate.save(configInfo, collectionName);
   }
}
