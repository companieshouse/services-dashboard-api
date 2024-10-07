package uk.gov.companieshouse.servicesdashboardapi.repository;

import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;

public interface CustomMongoConfigRepository {
   void saveConfigInfo(MongoConfigInfo configInfo);
}
