package uk.gov.companieshouse.servicesdashboardapi.repository;
import java.util.List;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;

public interface CustomMongoProjectInfoRepository {
   void saveProjectInfos(List<MongoProjectInfo> mongoProjectInfoList);
   // boolean existsByNameAndVersion(String name, String version);
   boolean existsByName(String name);
}
