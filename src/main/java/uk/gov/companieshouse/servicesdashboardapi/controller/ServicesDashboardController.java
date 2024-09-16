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
import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.github.GitInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.merge.ProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.sonar.SonarComponent;
import uk.gov.companieshouse.servicesdashboardapi.model.sonar.SonarProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.service.deptrack.GetAllProjects;
import uk.gov.companieshouse.servicesdashboardapi.service.github.GitService;
import uk.gov.companieshouse.servicesdashboardapi.service.sonar.SonarService;
import uk.gov.companieshouse.servicesdashboardapi.service.ServicesDashboardService;
import uk.gov.companieshouse.servicesdashboardapi.service.aws.EcsService;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

@RestController
public class ServicesDashboardController {

   private final ServicesDashboardService servicesDashboardService;
   private final GetAllProjects servicesDepTrack;

   @Value("${aws.envs}")
   private String[] awsEnvs;

   @Autowired
   private SonarService serviceSonar;

   @Autowired
   private ServicesInfo servicesInfo;

   @Autowired
   private GitService gitService;

   @Autowired
   private EcsService ecsService;

  @Autowired
  public ServicesDashboardController(ServicesDashboardService servicesDashboardService,
                                     GetAllProjects servicesDepTrack){
      this.servicesDashboardService = servicesDashboardService;
      this.servicesDepTrack = servicesDepTrack;
  }

  @GetMapping("/services-dashboard/list-services")
  public ResponseEntity<List<ProjectInfo>> listServices( ) {
      ApiLogger.info("---------list-services START");
      List<DepTrackProjectInfo> listDepTrack = this.servicesDepTrack.fetch();

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

         System.out.println(projectInfo);
      });

      for (String env : awsEnvs) {
         ecsService.fetchClusterInfo(env);
      }

      servicesDashboardService.createServicesDashboard();

      ApiLogger.info("---------list-services END");
      return new ResponseEntity<List<ProjectInfo>>(new ArrayList<>(projectInfoMap.values()), HttpStatus.OK);
  }
  @GetMapping("/services-dashboard/ecs")
  public ResponseEntity<String> sourceEcs( ) {
      for (String env : awsEnvs) {
         ecsService.fetchClusterInfo(env);
      }
      return new ResponseEntity<>("ECS ok", HttpStatus.OK);
   }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
      ApiLogger.info("Failure in integer conversion of Response's header total projects");
  }
}


