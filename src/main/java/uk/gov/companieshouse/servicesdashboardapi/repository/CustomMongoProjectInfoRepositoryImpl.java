package uk.gov.companieshouse.servicesdashboardapi.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.springframework.stereotype.Repository;

import java.util.List;

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
        this.collectionName = mongoConfig.getCollectionName();
    }

    @Override
    public void saveProjectInfos(List<MongoProjectInfo> mongoProjectInfoList) {
        for (MongoProjectInfo info : mongoProjectInfoList) {
            if (existsByName(info.getName())) {
               updateVersions(info);
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

    private void updateVersions(MongoProjectInfo newInfo) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(newInfo.getName()));

        MongoProjectInfo existingInfo = mongoTemplate.findOne(query, MongoProjectInfo.class, collectionName);

        if (existingInfo != null) {
            List<MongoVersionInfo> existingVersions = existingInfo.getVersions();
            List<MongoVersionInfo> newVersions = newInfo.getVersions();

            // Find versions that are not present in the existing document
            for (MongoVersionInfo newVersion : newVersions) {
                boolean versionExists = existingVersions.stream()
                        .anyMatch(existingVersion -> existingVersion.getVersion().equals(newVersion.getVersion()));
                if (!versionExists) {
                    existingVersions.add(newVersion);
                }
            }

            // Update the document with the new versions
            Update update = new Update();
            update.set("versions", existingVersions);

            mongoTemplate.updateFirst(query, update, MongoProjectInfo.class, collectionName);
        }
    }
}
