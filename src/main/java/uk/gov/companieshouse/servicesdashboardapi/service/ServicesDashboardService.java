package uk.gov.companieshouse.servicesdashboardapi.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.servicesdashboardapi.utils.ApiLogger;

@Service   
public class ServicesDashboardService {

//   @Autowired
  public ServicesDashboardService() {
  }

//   @Autowired
//   public ServicesDashboardService(
//       CustomerFeedbackMapper customerFeedbackMapper,
//       CustomerFeedbackRepository customerFeedbackRepository) {
//     this.customerFeedbackMapper = customerFeedbackMapper;
//     this.customerFeedbackRepository = customerFeedbackRepository;
//   }

   // public void createServicesDashboard(CustomerFeedbackDTO customerFeedbackDTO, String requestId)
   // throws SendEmailException {
   public void createServicesDashboard(String requestId){

    ApiLogger.debugContext(requestId, "Processing services dashboard");

  }
}
