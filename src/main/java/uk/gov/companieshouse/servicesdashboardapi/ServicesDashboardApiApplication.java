package uk.gov.companieshouse.servicesdashboardapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;

import uk.gov.companieshouse.servicesdashboardapi.controller.ServicesDashboardController;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import java.util.function.Function;

import uk.gov.companieshouse.servicesdashboardapi.lambda.CronEvent;


@SpringBootApplication
public class ServicesDashboardApiApplication {

  public static final String SDAPI_APP_NAMESPACE = "services-dashboard-api";
  private final ServicesDashboardController servicesController;

  public ServicesDashboardApiApplication(ServicesDashboardController servicesController) {
    this.servicesController = servicesController;
  }

  public static void main(String[] args) {
    SpringApplication.run(ServicesDashboardApiApplication.class, args);
  }

  @Bean
	public Function<CronEvent, Void> handleEvent() {
    ApiLogger.info("triggering event received");
    return event -> {
      if (event.getAction().equals("loadAllInfo")) {
        servicesController.loadAllInfo();
        ApiLogger.info("loadAllInfo");
      }
      return null;
    };
  }
}
