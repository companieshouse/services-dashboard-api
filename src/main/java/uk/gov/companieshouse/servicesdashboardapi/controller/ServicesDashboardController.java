package uk.gov.companieshouse.servicesdashboardapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.gov.companieshouse.servicesdashboardapi.model.merge.ServicesInfo;
import uk.gov.companieshouse.servicesdashboardapi.mapper.ConfigInfoMapper;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoConfigInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.endoflife.EndofLifeInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ConfigInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.sonar.SonarComponent;
import uk.gov.companieshouse.servicesdashboardapi.model.sonar.SonarProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoConfigRepository;
import uk.gov.companieshouse.servicesdashboardapi.repository.CustomMongoProjectInfoRepository;
import uk.gov.companieshouse.servicesdashboardapi.service.deptrack.GetAllProjects;
import uk.gov.companieshouse.servicesdashboardapi.service.endoflife.EndoflifeService;
import uk.gov.companieshouse.servicesdashboardapi.service.github.GitService;
import uk.gov.companieshouse.servicesdashboardapi.service.sonar.SonarService;
import uk.gov.companieshouse.servicesdashboardapi.service.ServicesDashboardService;
// import uk.gov.companieshouse.servicesdashboardapi.service.aws.EcsService;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

@RestController
public class ServicesDashboardController {

   private final ServicesDashboardService servicesDashboardService;
   private final GetAllProjects servicesDepTrack;

   @Value("${aws.envs}")
   private String[] awsEnvs;

   @Value("${deepScan.enabled}")
   private Boolean deepScanEnabled;

   @Autowired
   private SonarService serviceSonar;

   @Autowired
   private ServicesInfo servicesInfo;

   @Autowired
   private GitService gitService;

   // @Autowired
   // private EcsService ecsService;

   @Autowired
   private EndoflifeService endolService;
   @Autowired
   private CustomMongoConfigRepository customMongoConfigRepository;
   @Autowired
   private CustomMongoProjectInfoRepository customMongoProjectInfoRepository;



   @Autowired
   public ServicesDashboardController(ServicesDashboardService servicesDashboardService,
                                     GetAllProjects servicesDepTrack){
      this.servicesDashboardService = servicesDashboardService;
      this.servicesDepTrack = servicesDepTrack;
  }

   //   @GetMapping("/services-dashboard/ecs")
   //   public ResponseEntity<String> sourceEcs( ) {
   //       for (String env : awsEnvs) {
   //          ecsService.fetchClusterInfo(env);
   //       }
   //       return new ResponseEntity<>("ECS ok", HttpStatus.OK);
   //    }

   @GetMapping("/services-dashboard/list-services")
   public ResponseEntity<List<ProjectInfo>> listServices( ) {
      Map<String, ProjectInfo> projectInfoMap = loadListServices();
      return new ResponseEntity<List<ProjectInfo>>(new ArrayList<>(projectInfoMap.values()), HttpStatus.OK);
   }

   @GetMapping("/services-dashboard/endol")
   public ResponseEntity<String> sourceEndol( ) {
      loadListEol();
      return new ResponseEntity<>("Endol ok", HttpStatus.OK);
   }


   @ExceptionHandler(MethodArgumentTypeMismatchException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public void handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
         ApiLogger.info("Failure in integer conversion of Response's header total projects");
   }

   private void filterList(List<DepTrackProjectInfo> listDepTrack) {
      // if deepScanEnabled is false, then avoid querying (Sonar & GitHub)-apis for
      // uuids already known in Mongo
      if (!deepScanEnabled) {
         listDepTrack.removeIf(entry -> {
            boolean exists = customMongoProjectInfoRepository.existsByUuid(entry.getName(), entry.getUuid());
            if (exists) {
                  ApiLogger.info("Skipping [" + entry.getName() + "][" + entry.getUuid() + "] as it already exists in the database");
            }
            return exists;
         });
      }
  }

   private Map<String, ProjectInfo> loadListServices( ) {
      ApiLogger.info("---------list-services START");
      List<DepTrackProjectInfo> listDepTrack = this.servicesDepTrack.fetch();
      filterList(listDepTrack);

      Map<String, ProjectInfo> projectInfoMap = servicesInfo.setProjectInfoMap(listDepTrack);

      projectInfoMap.forEach((name, projectInfo) -> {
         ApiLogger.info("==============> working on " + name);
         SonarProjectInfo sonarInfo = serviceSonar.fetchMetrics(name);
         SonarComponent component = sonarInfo.getComponent();
         if (component != null) {
            projectInfo.setSonarKey(component.getKey());
            projectInfo.setSonarMetrics(component.getMeasures());
         }

         GitInfo gitInfo = gitService.getRepoInfo(name);
         projectInfo.setGitInfo(gitInfo);

         ApiLogger.info(projectInfo.toString());
      });

      // this is now done by https://github.com/companieshouse/services-dashboard-ecs
      // for (String env : awsEnvs) {
      //    ecsService.fetchClusterInfo(env);
      // }

      servicesDashboardService.createServicesDashboard();

      ApiLogger.info("---------list-services END");
      return projectInfoMap;
   }

   public void loadListEol( ) {
      ApiLogger.info("loadListEol START");
      Map<String, List<EndofLifeInfo>> endolMap = endolService.fetcEndofLives();
      ConfigInfo configInfo = new ConfigInfo ();
      configInfo.setEndol(endolMap);
      MongoConfigInfo mongoConfigInfo = ConfigInfoMapper.INSTANCE.configInfoToMongoConfigInfo(configInfo);
      customMongoConfigRepository.saveConfigInfo(mongoConfigInfo);
      ApiLogger.info("loadListEol END");
   }

   // when triggered by a scheduler/lambda, load both lists
   public void loadAllInfo(boolean deepscan ) {
      deepScanEnabled = deepscan;
      Map<String, ProjectInfo> projectInfoMap = loadListServices();
      loadListEol();
   }
}


