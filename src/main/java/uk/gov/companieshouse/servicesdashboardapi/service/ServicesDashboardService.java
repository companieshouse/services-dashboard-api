package uk.gov.companieshouse.servicesdashboardapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.config.MongoConfig;
import uk.gov.companieshouse.servicesdashboardapi.mapper.ProjectInfoMapper;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoProjectInfoRepository;
import uk.gov.companieshouse.servicesdashboardapi.repository.MongoProjectInfoRepository;


import java.util.List;
import java.util.stream.Collectors;
@Service   
public class ServicesDashboardService {

   // @Autowired
   // private MongoProjectInfoRepository mongoRepository;
   
    @Autowired
    private CustomMongoProjectInfoRepository customMongoProjectInfoRepository;

   // @Autowired
   // private MongoTemplate mongoTemplate;

   // @Autowired
   // private MongoConfig mongoConfig;

   public void createServicesDashboard(List<DepTrackProjectInfo> depTrackProjectInfoList, String requestId){

   ApiLogger.infoContext("10", "---------Create Serv[1]");
   insertProjects(depTrackProjectInfoList);
   ApiLogger.infoContext("10", "---------Create Serv[2]");
  }

  public void insertProjects(List<DepTrackProjectInfo> depTrackProjectInfoList) {
      List<MongoProjectInfo> mongoProjectInfoList = ProjectInfoMapper.INSTANCE.depTrackProjectInfoListToMongoProjectInfoList(depTrackProjectInfoList);
      customMongoProjectInfoRepository.saveProjectInfos(mongoProjectInfoList);

   //    for (MongoProjectInfo info : mongoProjectInfoList) {
   //       if (!existsByNameAndVersion(info.getName(), info.getVersion())) {
   //           mongoTemplate.insert(info, mongoConfig.getCollectionName());
   //       }
   //   }      
      // mongoTemplate.insert(mongoProjectInfoList,  mongoConfig.getCollectionName());
      // mongoRepository.saveAll(mongoProjectInfoList);

      // List<MongoProjectInfo> filteredProjectInfoList = filterExistingProjects(mongoProjectInfoList);
      // mongoTemplate.insertAll(filteredProjectInfoList);
      // mongoTemplate.insert(filteredProjectInfoList, mongoConfig.getCollectionName());
      // String collectionName = mongoConfig.getCollectionName();
      // for (MongoProjectInfo projectInfo : filteredProjectInfoList) {
      //    mongoTemplate.save(projectInfo, collectionName);
      // }
   }
//    public void addNewProjects(List<MongoProjectInfo> mongoProjectInfoList) {
//       for (MongoProjectInfo projectInfo : mongoProjectInfoList) {
//           // Check if the project with the same name and version exists
//           if (!mongoRepository.findByNameAndVersion(projectInfo.getName(), projectInfo.getVersion()).isPresent()) {
//               // If not, save the new project
//               mongoRepository.save(projectInfo);
//           }
//       }
//   }
   private boolean existsByNameAndVersion(String name, String version) {
      Query query = new Query();
      query.addCriteria(Criteria.where("name").is(name).and("version").is(version));
      return mongoTemplate.exists(query, MongoProjectInfo.class, mongoConfig.getCollectionName());
   }

   // private List<MongoProjectInfo> filterExistingProjects(List<MongoProjectInfo> projectList) {
   //    return projectList.stream()
   //       .filter(project -> !existsByNameAndVersion(project.getName(), project.getVersion()))
   //       .collect(Collectors.toList());
   // }
}


