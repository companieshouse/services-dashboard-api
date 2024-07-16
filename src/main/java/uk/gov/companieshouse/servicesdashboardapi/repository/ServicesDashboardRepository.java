package uk.gov.companieshouse.servicesdashboardapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;

import java.util.List;

@Repository
public interface ServicesDashboardRepository extends MongoRepository<MongoProjectInfo, String> {
   // List<MongoProjectInfo> findBytName(String name);
}
