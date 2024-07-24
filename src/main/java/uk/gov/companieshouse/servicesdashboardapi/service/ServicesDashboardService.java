package uk.gov.companieshouse.servicesdashboardapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.mapper.ProjectInfoMapper;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoProjectInfoRepository;
@Service   
public class ServicesDashboardService {
   
    @Autowired
    private CustomMongoProjectInfoRepository customMongoProjectInfoRepository;

   public void createServicesDashboard(List<DepTrackProjectInfo> depTrackProjectInfoList, String requestId){

   ApiLogger.info("---------Create Serv START");
   insertProjects(depTrackProjectInfoList);
   ApiLogger.info("---------Create Serv END");
  }

  public void insertProjects(List<DepTrackProjectInfo> depTrackProjectInfoList) {
      List<MongoProjectInfo> mongoProjectInfoList = ProjectInfoMapper.INSTANCE.depTrackProjectInfoListToMongoProjectInfoList(depTrackProjectInfoList);
      customMongoProjectInfoRepository.saveProjectInfos(mongoProjectInfoList);
   }

}


