package uk.gov.companieshouse.servicesdashboardapi.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;

import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import com.mongodb.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

   @Autowired
    private MongoProperties mongoProperties;

   public String getCollectionName() {
      return  mongoProperties.getCollectionName();
   }

  @Bean
    public MongoClient mongoClient() {
        String auth = (mongoProperties.getUser().isEmpty() ? "" :
                  String.format("%s:%s@",
                     mongoProperties.getUser(),
                     mongoProperties.getPassword()));

        String uri = String.format("%s://%s%s/%s",
            mongoProperties.getProtocol(),
            auth,
            mongoProperties.getHostandport(),
            mongoProperties.getDbname());

        ApiLogger.debug("Connection string: " + uri);
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoDatabaseFactory mongoDbFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoClient(), mongoProperties.getDbname());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
         MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory()), new MongoMappingContext());
         converter.setTypeMapper(new DefaultMongoTypeMapper(null));
         MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), converter);

         return mongoTemplate;
    }
}




