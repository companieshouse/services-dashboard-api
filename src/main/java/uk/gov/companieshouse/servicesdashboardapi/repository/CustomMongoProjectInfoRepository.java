package uk.gov.companieshouse.servicesdashboardapi.repository;
import java.util.Set;
import java.util.Optional;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;

public interface CustomMongoProjectInfoRepository {
   void saveProjectInfos(Set<MongoProjectInfo> mongoProjectInfoSet);
   boolean existsByName(String name);
   Optional<MongoProjectInfo> findByName(String name);
}
