package uk.gov.companieshouse.servicesdashboardapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ServicesInfo;
import uk.gov.companieshouse.servicesdashboardapi.mapper.ProjectInfoMapper;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoProjectInfoRepository;
@Service
public class ServicesDashboardService {

   @Autowired
   private ServicesInfo servicesInfo;

   @Autowired
   private CustomMongoProjectInfoRepository customMongoProjectInfoRepository;

   public void createServicesDashboard(){
      ApiLogger.info("---------Create Serv START");
      List<MongoProjectInfo> mongoProjectInfoList = ProjectInfoMapper.INSTANCE.mapProjectInfoMap(servicesInfo.getProjectInfoMap());
      customMongoProjectInfoRepository.saveProjectInfos(mongoProjectInfoList);
      ApiLogger.info("---------Create Serv END");
   }
}


