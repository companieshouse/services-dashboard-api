package uk.gov.companieshouse.servicesdashboardapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.mapper.ProjectInfoMapper;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoProjectInfoRepository;
import uk.gov.companieshouse.servicesdashboardapi.service.sonar.SonarService;
@Service
public class ServicesDashboardService {

    @Autowired
    private CustomMongoProjectInfoRepository customMongoProjectInfoRepository;

    @Autowired
    private SonarService serviceSonar;

   public void createServicesDashboard(List<ProjectInfo> projectInfoList, String requestId){
      for (ProjectInfo p : projectInfoList) {
            System.out.println("==============> working on " + p.getName());
            serviceSonar.fetchMetrics(p.getName());
      }
      ApiLogger.info("---------Create Serv START");
      insertProjects(projectInfoList);
      ApiLogger.info("---------Create Serv END");
   }

   public void insertProjects(List<ProjectInfo> projectInfoList) {
      List<MongoProjectInfo> mongoProjectInfoList = ProjectInfoMapper.INSTANCE.projectInfoListToMongoProjectInfoList(projectInfoList);
      customMongoProjectInfoRepository.saveProjectInfos(mongoProjectInfoList);
   }
}


