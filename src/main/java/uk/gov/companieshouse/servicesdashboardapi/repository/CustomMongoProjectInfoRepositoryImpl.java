package uk.gov.companieshouse.servicesdashboardapi.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import uk.gov.companieshouse.servicesdashboardapi.config.MongoConfig;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;

@Repository
public class CustomMongoProjectInfoRepositoryImpl implements CustomMongoProjectInfoRepository {

    private final MongoTemplate mongoTemplate;
    private final String collectionName;

    public CustomMongoProjectInfoRepositoryImpl(MongoTemplate mongoTemplate,
                                                MongoConfig mongoConfig) {
        this.mongoTemplate = mongoTemplate;
        this.collectionName = mongoConfig.getCollectionName();
    }

    @Override
    public void saveProjectInfos(List<MongoProjectInfo> mongoProjectInfoList) {
        for (MongoProjectInfo info : mongoProjectInfoList) {
            if (!existsByNameAndVersion(info.getName(), info.getVersion())) {
                mongoTemplate.insert(info, collectionName);
            }
        }
    }

    @Override
    public boolean existsByNameAndVersion(String name, String version) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name).and("version").is(version));
        return mongoTemplate.exists(query, MongoProjectInfo.class, collectionName);
    }
}
