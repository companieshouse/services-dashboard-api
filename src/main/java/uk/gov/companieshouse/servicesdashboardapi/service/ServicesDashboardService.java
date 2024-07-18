package uk.gov.companieshouse.servicesdashboardapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.mapper.ProjectInfoMapper;

import java.util.List;
import java.util.stream.Collectors;
@Service   
public class ServicesDashboardService {

   @Autowired
   private MongoTemplate mongoTemplate;

   public void createServicesDashboard(List<DepTrackProjectInfo> depTrackProjectInfoList, String requestId){

   ApiLogger.infoContext("10", "---------Create Serv[1]");
   insertProjects(depTrackProjectInfoList);
   ApiLogger.infoContext("10", "---------Create Serv[2]");
  }

  public void insertProjects(List<DepTrackProjectInfo> depTrackProjectInfoList) {
   List<MongoProjectInfo> mongoProjectInfoList = ProjectInfoMapper.INSTANCE.depTrackProjectInfoListToMongoProjectInfoList(depTrackProjectInfoList);
   List<MongoProjectInfo> filteredProjectInfoList = filterExistingProjects(mongoProjectInfoList);
   mongoTemplate.insertAll(filteredProjectInfoList);
   }

   private boolean existsByNameAndVersion(String name, String version) {
      Query query = new Query();
      query.addCriteria(Criteria.where("name").is(name).and("version").is(version));
      return mongoTemplate.exists(query, MongoProjectInfo.class);
   }

   private List<MongoProjectInfo> filterExistingProjects(List<MongoProjectInfo> projectList) {
      return projectList.stream()
         .filter(project -> !existsByNameAndVersion(project.getName(), project.getVersion()))
         .collect(Collectors.toList());
   }
}


