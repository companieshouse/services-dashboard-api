package uk.gov.companieshouse.servicesdashboardapi;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.servicesdashboardapi.controller.ServicesDashboardController;
import uk.gov.companieshouse.servicesdashboardapi.lambda.CronEvent;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ServicesDashboardApiApplicationTest {

    private static CronEvent cronEvent(String action, boolean deepscan) {
        CronEvent event = new CronEvent();
        CronEvent.Detail detail = new CronEvent.Detail();
        detail.setAction(action);
        detail.setDeepscan(deepscan);
        event.setDetail(detail);
        return event;
    }

    @Test
    void shouldInvokeLoadAllInfoWhenActionIsLoadAllInfo() {
        ServicesDashboardController controller = mock(ServicesDashboardController.class);
        ServicesDashboardApiApplication application = new ServicesDashboardApiApplication(controller);
        Function<CronEvent, Void> handler = application.handleEvent();

        CronEvent event = cronEvent("loadAllInfo", true);

        assertNull(handler.apply(event));
        verify(controller).loadAllInfo(true);
        verify(controller, never()).loadListEol();
    }

    @Test
    void shouldInvokeLoadListEolWhenActionIsLoadEol() {
        ServicesDashboardController controller = mock(ServicesDashboardController.class);
        ServicesDashboardApiApplication application = new ServicesDashboardApiApplication(controller);
        Function<CronEvent, Void> handler = application.handleEvent();

        CronEvent event = cronEvent("loadEol", false);

        assertNull(handler.apply(event));
        verify(controller).loadListEol();
        verify(controller, never()).loadAllInfo(org.mockito.ArgumentMatchers.anyBoolean());
    }

    @Test
    void shouldNotInvokeControllerWhenDetailIsNull() {
        ServicesDashboardController controller = mock(ServicesDashboardController.class);
        ServicesDashboardApiApplication application = new ServicesDashboardApiApplication(controller);
        Function<CronEvent, Void> handler = application.handleEvent();

        CronEvent event = new CronEvent();

        assertNull(handler.apply(event));
        verify(controller, never()).loadAllInfo(org.mockito.ArgumentMatchers.anyBoolean());
        verify(controller, never()).loadListEol();
    }

    @Test
    void shouldNotInvokeControllerWhenActionIsNull() {
        ServicesDashboardController controller = mock(ServicesDashboardController.class);
        ServicesDashboardApiApplication application = new ServicesDashboardApiApplication(controller);
        Function<CronEvent, Void> handler = application.handleEvent();

        CronEvent event = cronEvent(null, false);

        assertNull(handler.apply(event));
        verify(controller, never()).loadAllInfo(org.mockito.ArgumentMatchers.anyBoolean());
        verify(controller, never()).loadListEol();
    }

    @Test
    void shouldNotInvokeControllerWhenActionIsUnsupported() {
        ServicesDashboardController controller = mock(ServicesDashboardController.class);
        ServicesDashboardApiApplication application = new ServicesDashboardApiApplication(controller);
        Function<CronEvent, Void> handler = application.handleEvent();

        CronEvent event = cronEvent("unsupported", false);

        assertNull(handler.apply(event));
        verify(controller, never()).loadAllInfo(org.mockito.ArgumentMatchers.anyBoolean());
        verify(controller, never()).loadListEol();
    }

    @Test
    void shouldThrowWhenEventIsNull() {
        ServicesDashboardController controller = mock(ServicesDashboardController.class);
        ServicesDashboardApiApplication application = new ServicesDashboardApiApplication(controller);
        Function<CronEvent, Void> handler = application.handleEvent();

        assertThrows(NullPointerException.class, () -> handler.apply(null));
    }

}
