package uk.gov.companieshouse.servicesdashboardapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.gov.companieshouse.servicesdashboardapi.mapper.MergeInfoMapper;
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
   @Autowired
   private SonarService serviceSonar;

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

      Map<String, ProjectInfo> projectInfoMap = MergeInfoMapper.INSTANCE.mapDepTrackListToProjectInfoMap(listDepTrack);

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
      String env = "cidev";
      ApiLogger.info("=======> STARTING SOURCING ECS: " + env);
      Map<String, Set<String>> ecsInfo = ecsService.fetchClusterInfo();
      for (Map.Entry<String, Set<String>> entry : ecsInfo.entrySet()) {
         String name = entry.getKey();
         Set<String> versions = entry.getValue();

         // Check if the key is present in secondMap
         if (!projectInfoMap.containsKey(name)) {
               // Log if key is not present in secondMap
               ApiLogger.info("ECS '" + name + "' not present in DT");
         } else {
               // Update the ecs map in the ProjectInfo for the given key
               ProjectInfo projectInfo = projectInfoMap.get(name);
               // Check if ecs is null or empty, and initialize if needed
               if (projectInfo.getEcs() == null || projectInfo.getEcs().isEmpty()) {
                     projectInfo.setEcs(new HashMap<>());
               }
               projectInfo.getEcs().computeIfAbsent(env, k -> new HashSet<>()).addAll(versions);
         }
      }
      ApiLogger.info("=======> COMPLETED SOURCING ECS: " + env);

      this.servicesDashboardService.createServicesDashboard(projectInfoMap,"aaaaa");

      ApiLogger.info("---------list-services END");
      return new ResponseEntity<List<ProjectInfo>>(new ArrayList<>(projectInfoMap.values()), HttpStatus.OK);
  }
  @GetMapping("/services-dashboard/ecs")
  public ResponseEntity<String> sourceEcs( ) {
      ecsService.fetchClusterInfo();
      return new ResponseEntity<>("ECS ok", HttpStatus.OK);
   }


  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
      ApiLogger.info("Failure in integer conversion of Response's header total projects");
  }
}


