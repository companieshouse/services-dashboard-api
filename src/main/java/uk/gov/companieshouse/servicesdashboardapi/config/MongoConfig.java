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
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;

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
        // Register the standard java.time (Jsr310) converters so types such as Instant/LocalDate
        // used in DAO models are recognised as simple types rather than entities to introspect.
        MongoCustomConversions customConversions = new MongoCustomConversions(Collections.emptyList());

        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        mappingContext.afterPropertiesSet();

        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), mappingContext);
        converter.setCustomConversions(customConversions);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.afterPropertiesSet();

        return new MongoTemplate(mongoDbFactory, converter);
    }
}


