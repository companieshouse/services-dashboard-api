package uk.gov.companieshouse.servicesdashboardapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import uk.gov.companieshouse.servicesdashboardapi.controller.ServicesDashboardController;
import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

import java.net.InetAddress;
import java.util.function.Function;

import uk.gov.companieshouse.servicesdashboardapi.lambda.CronEvent;


import java.io.BufferedReader;
import java.io.InputStreamReader;

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
    testDNS("dependency-track.companieshouse.gov.uk");
  }

  private static void logHostInfo(String hostName) {
    try {
      InetAddress address = InetAddress.getByName(hostName);
      ApiLogger.info("Resolved address: " + address);
    } catch (Exception e) {
      ApiLogger.info("Failed to resolve address: " + e.getMessage());
    }
  }



  private  void testDNS(String hostName) {
        String nslookupResult = runCommand("nslookup " + hostName);
        String digResult = runCommand("dig " + hostName);
        ApiLogger.info( String.format("NSLOOKUP Result:%s / DIG Result:%s", nslookupResult, digResult));
    }

    private String runCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            output.append("Error running command: ").append(e.getMessage()).append("\n");
        }
        return output.toString();
    }





  public static void main(String[] args) {
    SpringApplication.run(ServicesDashboardApiApplication.class, args);
  }

  @Bean
  public Function<CronEvent, Void> handleEvent() {
      ApiLogger.info("triggering event received");
      return event -> {
          if (event.getDetail() != null && "loadAllInfo".equals(event.getDetail().getAction())) {
              servicesController.loadAllInfo();
              ApiLogger.info("loadAllInfo");
          }
          return null;
      };
  }
}
