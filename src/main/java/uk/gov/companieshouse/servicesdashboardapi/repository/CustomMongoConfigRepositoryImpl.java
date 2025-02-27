package uk.gov.companieshouse.servicesdashboardapi.repository;

import java.time.LocalDateTime;
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

      Query query = new Query(Criteria.where("_id").is(singletonId));
      Update update = new Update()
         .set("lastScan", LocalDateTime.now()
         .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

      System.out.println("Query: " + query);
      System.out.println("Update: " + update);

      // Try first to update if the doc exists
      if (mongoTemplate.updateFirst(query, update, collectionName).getModifiedCount() == 0) {
         // If nothing was updated --> create it
         ApiLogger.info("creating new config info");
         mongoTemplate.upsert(query, update, collectionName);
      }
      ApiLogger.info("saveConfigInfo END");
   }
}
