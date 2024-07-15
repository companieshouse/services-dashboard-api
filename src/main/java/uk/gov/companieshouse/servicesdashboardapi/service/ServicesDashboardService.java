package uk.gov.companieshouse.servicesdashboardapi.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.repository.ServicesDashboardRepository;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.mapper.ProjectInfoMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

// @Configuration
@Service   
public class ServicesDashboardService {

   // @Autowired
   // private ProjectInfoMapper projectInfoMapper;
   // @Autowired
   // private ServicesDashboardRepository servicesDashboardRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

   // @Autowired
   // private ServicesDashboardRepository servicesDashboardRepository;
   // private final ProjectInfoMapper projectInfoMapper;
   // private final ServicesDashboardRepository servicesDashboardRepository;

   // @Autowired
   // public ServicesDashboardService(
   //    ProjectInfoMapper projectInfoMapper,
   //    ServicesDashboardRepository servicesDashboardRepository) {
   // this.projectInfoMapper = projectInfoMapper;
   // this.servicesDashboardRepository = servicesDashboardRepository;
   // }

   public void createServicesDashboard(List<DepTrackProjectInfo> depTrackProjectInfo, String requestId){

   //var mongoProjectInfo = projectInfoMapper.toDao(depTrackProjectInfo);

   ApiLogger.infoContext("10", "---------Create Serv[1]");
   //MongoProjectInfo mongoProjectInfo = new MongoProjectInfo();
   // mongoTemplate.save(mongoProjectInfo);
   mongoTemplate.insertAll(depTrackProjectInfo);
   
   // servicesDashboardRepository.save(mongoProjectInfo);

   //  ApiLogger.debugContext(requestId, "Processing services dashboard");

   ApiLogger.infoContext("10", "---------Create Serv[2]");
   // MongoProjectInfo mongoProjectInfo =
   // ServicesDashboardRepository.insert(mongoProjectInfo);

  }
}
