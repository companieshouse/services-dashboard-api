package uk.gov.companieshouse.servicesdashboardapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.List;

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
  public ServicesDashboardController(ServicesDashboardService servicesDashboardService,
                                     GetAllProjects servicesDepTrack){
      this.servicesDashboardService = servicesDashboardService;
      this.servicesDepTrack = servicesDepTrack;
  }

  @GetMapping("/services-dashboard/list-services")
  public ResponseEntity<List<ProjectInfo>> listServices( ) {
      ApiLogger.info("---------list-services START");
      List<DepTrackProjectInfo> listDepTrack = this.servicesDepTrack.fetch();

      List<ProjectInfo> projectInfoList = MergeInfoMapper.INSTANCE.mapList(listDepTrack);

      for (ProjectInfo p : projectInfoList) {
         System.out.print("==============> working on " + p.getName());
         SonarProjectInfo sonarInfo = serviceSonar.fetchMetrics(p.getName());
         SonarComponent component = sonarInfo.getComponent();
         if (component != null) {
            p.setSonarMetrics(component.getMeasuresAsMap());
         }

         GitInfo gitInfo = gitService.getRepoInfo(p.getName());
         p.setGitInfo(gitInfo);

         System.out.println(p);

      }
      this.servicesDashboardService.createServicesDashboard(projectInfoList,"aaaaa");

      ApiLogger.info("---------list-services END");
      return new ResponseEntity<>(projectInfoList, HttpStatus.OK);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
      ApiLogger.info("Failure in integer conversion of Response's header total projects");
  }
}


