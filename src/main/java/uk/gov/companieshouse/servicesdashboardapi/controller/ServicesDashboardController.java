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

import uk.gov.companieshouse.servicesdashboardapi.model.deptrack.DepTrackProjectInfo;
import uk.gov.companieshouse.servicesdashboardapi.service.deptrack.GetAllProjects;
import uk.gov.companieshouse.servicesdashboardapi.service.ServicesDashboardService;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

@RestController
public class ServicesDashboardController {

   private final ServicesDashboardService servicesDashboardService;
   private final GetAllProjects servicesDepTrack;
  
  @Autowired
  public ServicesDashboardController(ServicesDashboardService servicesDashboardService, GetAllProjects servicesDepTrack) {
      this.servicesDashboardService = servicesDashboardService;
      this.servicesDepTrack = servicesDepTrack;
  }

  @GetMapping("/services-dashboard/list-services")
  public ResponseEntity<List<DepTrackProjectInfo>> listServices( ) {
   ApiLogger.info("---------list-services START");
   List<DepTrackProjectInfo> list = this.servicesDepTrack.fetch();
   this.servicesDashboardService.createServicesDashboard(list,"aaaaa");

   ApiLogger.info("---------list-services END");
   return new ResponseEntity<>(list, HttpStatus.OK);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
      ApiLogger.info("Failure in integer conversion of Response's header total projects");
  }
}


