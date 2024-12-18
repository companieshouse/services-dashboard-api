package uk.gov.companieshouse.servicesdashboardapi.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import uk.gov.companieshouse.servicesdashboardapi.controller.ServicesDashboardController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HandlerCronEvent implements RequestHandler<ScheduledEvent, Void> {

    @Autowired
    private ServicesDashboardController ServicesController;

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("triggering event received\n");
        Map<String, Object> detail = event.getDetail();

        if (detail.containsKey("action") && detail.get("action") instanceof String) {

            String action = (String) detail.get("action");
            logger.log("Action value: " + action + "\n");

            if (action.equals("loadInfo")) {
                ServicesController.loadInfo();
                System.out.println("loadInfo");
            }

        } else {
            logger.log("'action' field is missing or not a string\n");
        }
        return null;
    }
}