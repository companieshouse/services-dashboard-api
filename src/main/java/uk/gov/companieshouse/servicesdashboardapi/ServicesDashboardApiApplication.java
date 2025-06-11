package uk.gov.companieshouse.servicesdashboardapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import uk.gov.companieshouse.servicesdashboardapi.controller.ServicesDashboardController;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import java.net.InetAddress;
import java.util.function.Function;

import uk.gov.companieshouse.servicesdashboardapi.lambda.CronEvent;


// @EnableAutoConfiguration
// @Import({
//     ContextFunctionCatalogAutoConfiguration.class
// })
@SpringBootApplication
public class ServicesDashboardApiApplication {

  public static final String SDAPI_APP_NAMESPACE = "services-dashboard-api";
  private final ServicesDashboardController servicesController;

  public ServicesDashboardApiApplication(ServicesDashboardController servicesController) {
    this.servicesController = servicesController;
    logHostInfo("dependency-track.companieshouse.gov.uk");
    logHostInfo("code-analysis.platform.aws.chdev.org");
    logHostInfo("api.github.com");
    logHostInfo("endoflife.date");
  }

  private static void logHostInfo(String hostName) {
    try {
      InetAddress address = InetAddress.getByName(hostName);
      ApiLogger.info("Resolved address: " + address);
    } catch (Exception e) {
      ApiLogger.info("Failed to resolve address: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(ServicesDashboardApiApplication.class, args);
  }

  @Bean
  public Function<CronEvent, Void> handleEvent() {
      ApiLogger.info("trigger event received");
      return event -> {
            if (event.getDetail() != null) {

            String action = event.getDetail().getAction();
            if (action == null) {
              ApiLogger.info("Action is undefined/null");
            } else {
              ApiLogger.info("Action: " + action);
              switch (action) {
                case "loadAllInfo":
                  servicesController.loadAllInfo(event.getDetail().isDeepscan());
                  break;
                case "loadEol":
                  servicesController.loadListEol();
                  break;
                default:
                  ApiLogger.info("Unsupported action");
                  break;
              }
            }
          }
          return null;
      };
  }
}
