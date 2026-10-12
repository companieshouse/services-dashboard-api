package uk.gov.companieshouse.servicesdashboardapi.repository;

import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;

import java.util.List;
import java.util.Optional;

public interface CustomMongoProjectInfoRepository {
    void saveProjectInfos(List<MongoProjectInfo> mongoProjectInfoList);

    boolean existsByName(String name);

    boolean existsByUuid(String name, String uuid);

    Optional<MongoProjectInfo> findByName(String name);
}
