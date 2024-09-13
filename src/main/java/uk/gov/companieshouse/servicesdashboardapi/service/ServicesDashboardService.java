package uk.gov.companieshouse.servicesdashboardapi.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.mapper.ProjectInfoMapper;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoProjectInfoRepository;
@Service
public class ServicesDashboardService {

    @Autowired
    private CustomMongoProjectInfoRepository customMongoProjectInfoRepository;

   public void createServicesDashboard(Map<String, ProjectInfo> projectInfoMap, String requestId){
      ApiLogger.info("---------Create Serv START");
      insertProjects(projectInfoMap);
      ApiLogger.info("---------Create Serv END");
   }

   public void insertProjects(Map<String, ProjectInfo> projectInfoMap) {
      List<MongoProjectInfo> mongoProjectInfoList = ProjectInfoMapper.INSTANCE.mapProjectInfoMap(projectInfoMap);
      customMongoProjectInfoRepository.saveProjectInfos(mongoProjectInfoList);
   }
}


