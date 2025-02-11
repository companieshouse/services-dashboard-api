package uk.gov.companieshouse.servicesdashboardapi.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import uk.gov.companieshouse.servicesdashboardapi.config.MongoConfig;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

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
      ApiLogger.info("saveConfigInfo START");

      Query query = new Query(Criteria.where("id").is(singletonId));
      Update update = new Update()
         .set("lastScan", LocalDateTime.ofInstant(
               Instant.ofEpochMilli(System.currentTimeMillis()),
               ZoneId.systemDefault())
               .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

      mongoTemplate.upsert(query, update, MongoConfigInfo.class, collectionName);
      ApiLogger.info("saveConfigInfo END");
   }
}
