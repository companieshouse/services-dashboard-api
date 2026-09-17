package uk.gov.companieshouse.servicesdashboardapi.lambda;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CronEventTest {

    /**
     * Test case to verify getDetail returns the correct Detail object.
     */
    @Test
    void testGetDetailReturnsCorrectDetail() {
        // Arrange
        CronEvent cronEvent = new CronEvent();
        CronEvent.Detail detail = new CronEvent.Detail();
        detail.setAction("action1");
        detail.setDeepscan(true);

        // Act
        cronEvent.setDetail(detail);

        // Assert
        assertNotNull(cronEvent.getDetail());
        assertEquals("action1", cronEvent.getDetail().getAction());
        assertTrue(cronEvent.getDetail().isDeepscan());
    }

    /**
     * Test case to verify getDetail returns null when no Detail object is set.
     */
    @Test
    void testGetDetailReturnsNullWhenNotSet() {
        // Arrange
        CronEvent cronEvent = new CronEvent();

        // Act and Assert
        assertNull(cronEvent.getDetail());
    }

    /**
     * Test case to verify CronEvent keeps the same Detail reference that is set.
     */
    @Test
    void testSetDetailWithModifiedDetailObject() {
        // Arrange
        CronEvent cronEvent = new CronEvent();
        CronEvent.Detail detail = new CronEvent.Detail();
        detail.setAction("initialAction");
        detail.setDeepscan(false);

        // Act
        cronEvent.setDetail(detail);
        detail.setAction("modifiedAction");
        detail.setDeepscan(true);

        // Assert
        assertEquals("modifiedAction", cronEvent.getDetail().getAction());
        assertTrue(cronEvent.getDetail().isDeepscan());
    }
}
