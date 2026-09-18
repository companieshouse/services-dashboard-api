package uk.gov.companieshouse.servicesdashboardapi.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

    private final MongoProperties mongoProperties;

    public MongoConfig(MongoProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }

    public String getCollectionNameProj() {
        return mongoProperties.getCollectionNameProj();
    }

    public String getCollectionNameConf() {
        return mongoProperties.getCollectionNameConf();
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

        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoDatabaseFactory mongoDbFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDbname());
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory) {
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return new MongoTemplate(mongoDbFactory, converter);
    }
}


