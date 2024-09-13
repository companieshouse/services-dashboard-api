package uk.gov.companieshouse.servicesdashboardapi.repository;
import java.util.List;
import java.util.Optional;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;

public interface CustomMongoProjectInfoRepository {
   void saveProjectInfos(List<MongoProjectInfo> mongoProjectInfoList);
   boolean existsByName(String name);
   Optional<MongoProjectInfo> findByName(String name);
}
