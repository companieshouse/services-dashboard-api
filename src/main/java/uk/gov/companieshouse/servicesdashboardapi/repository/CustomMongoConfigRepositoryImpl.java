package uk.gov.companieshouse.servicesdashboardapi.repository;

import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.stereotype.Repository;

import uk.gov.companieshouse.servicesdashboardapi.config.MongoConfig;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;


@Repository
public class CustomMongoConfigRepositoryImpl implements CustomMongoConfigRepository {

   private final MongoTemplate mongoTemplate;
   private final String collectionName;

   public CustomMongoConfigRepositoryImpl(MongoTemplate mongoTemplate,
                                          MongoConfig mongoConfig) {
      this.mongoTemplate = mongoTemplate;
      this.collectionName = mongoConfig.getCollectionNameConf();
      System.out.println("============================ SAVING TO " + this.collectionName);
   }

   @Override
   public void saveConfig(MongoConfigInfo mongoConfigInfo) {
      mongoTemplate.insert(mongoConfigInfo, collectionName);
   }
}
