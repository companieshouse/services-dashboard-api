package uk.gov.companieshouse.servicesdashboardapi.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import uk.gov.companieshouse.servicesdashboardapi.controller.ServicesDashboardController;

import org.springframework.stereotype.Component;


@Component
public class HandlerCronEvent implements RequestHandler<ScheduledEvent, Void> {

    private static final String ACTION = "action";
    private ServicesDashboardController servicesController;

    public HandlerCronEvent(ServicesDashboardController servicesController) {
        this.servicesController = servicesController;
    }

    @Override
    public Void handleRequest(ScheduledEvent event, Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("triggering event received\n");

        Map<String, Object> detail = event.getDetail();

        if (detail.containsKey(ACTION) && detail.get(ACTION) instanceof String action) {

            logger.log("Action value: " + action + "\n");

            if (action.equals("loadAllInfo")) {
                servicesController.loadAllInfo();
                logger.log("loadAllInfo");
            }

        } else {
            logger.log("'action' field is missing or not a string\n");
        }
        return null;
    }
}