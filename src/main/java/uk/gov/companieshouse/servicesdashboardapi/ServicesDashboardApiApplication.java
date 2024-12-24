package uk.gov.companieshouse.servicesdashboardapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServicesDashboardApiApplication {

  public static final String SDAPI_APP_NAMESPACE = "services-dashboard-api";

  public static void main(String[] args) {
    SpringApplication.run(ServicesDashboardApiApplication.class, args);
  }
}
