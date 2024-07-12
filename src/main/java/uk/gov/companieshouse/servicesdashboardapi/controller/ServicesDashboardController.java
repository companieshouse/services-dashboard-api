package uk.gov.companieshouse.servicesdashboardapi.controller;

import static uk.gov.companieshouse.servicesdashboardapi.utils.Constants.HEADER_KEY_TOT_COUNT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.List;

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.model.dao.MongoProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.repository.ServicesDashboardRepository;
import uk.gov.companieshouse.servicesdashboardapi.service.ServicesDashboardService;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;
import uk.gov.companieshouse.servicesdashboardapi.service.dtgetallprojects.GetAllProjects;



@RestController
public class ServicesDashboardController {

   private final ServicesDashboardService servicesDashboardService;
   private final GetAllProjects servicesDepTrack;
  

  @Autowired
  public ServicesDashboardController(ServicesDashboardService servicesDashboardService, GetAllProjects servicesDepTrack) {
   this.servicesDashboardService = servicesDashboardService;
   this.servicesDepTrack = servicesDepTrack;
}

  private int getP(   
   @RequestHeader(value = HEADER_KEY_TOT_COUNT) int proj_tot_count
  ) {
     return proj_tot_count;
  }    

  @GetMapping("/services-dashboard/list-services")
  public ResponseEntity<List<DepTrackProjectInfo>> feedback(
   // DepTrackProjectInfo depTrackProjectInfo
  ) {
   //int i = this.getP();
   ApiLogger.infoContext("10", "---------ALL OK");
   List<DepTrackProjectInfo> list = this.servicesDepTrack.fetch();
   // this.servicesDashboardService.createServicesDashboard(depTrackProjectInfo,"aaaaa");
   this.servicesDashboardService.createServicesDashboard(list,"aaaaa");

   return new ResponseEntity<>(list, HttpStatus.OK);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
      ApiLogger.infoContext("10", "Failure in integer conversion of Response's header total projects");
  }

}


