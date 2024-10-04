package uk.gov.companieshouse.servicesdashboardapi.repository;

import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;

public interface CustomMongoConfigRepository {
   void saveConfig(MongoConfigInfo mongoConfigInfo);
}
