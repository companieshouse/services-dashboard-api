package uk.gov.companieshouse.servicesdashboardapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.servicesdashboardapi.model.sonar.SonarComponent;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeserializerSonarMeasuresTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeserializeMeasuresFromValueAndPeriodNodes() throws Exception {
        String json = """
                {
                  "key": "service-key",
                  "name": "service-name",
                  "measures": [
                	{ "metric": "code_smells", "value": "514", "bestValue": false },
                	{ "metric": "new_coverage", "period": { "index": 1, "value": "90.6", "bestValue": false } }
                  ]
                }
                """;

        SonarComponent component = objectMapper.readValue(json, SonarComponent.class);

        assertEquals(514, component.getMeasures().get("code_smells"));
        assertEquals(91, component.getMeasures().get("new_coverage"));
    }

    @Test
    void shouldIgnoreEntriesWithoutMetricOrValue() throws Exception {
        String json = """
                {
                  "key": "service-key",
                  "name": "service-name",
                  "measures": [
                	{ "metric": "valid_metric", "value": "10" },
                	{ "value": "99" },
                	{ "metric": "missing_value" },
                	{ "metric": "null_value", "value": null },
                	{ "metric": "null_period", "period": null }
                  ]
                }
                """;

        SonarComponent component = objectMapper.readValue(json, SonarComponent.class);
        Map<String, Integer> measures = component.getMeasures();

        assertEquals(1, measures.size());
        assertEquals(10, measures.get("valid_metric"));
    }

    @Test
    void shouldReturnEmptyMapWhenMeasuresArrayIsEmpty() throws Exception {
        String json = """
                {
                  "key": "service-key",
                  "name": "service-name",
                  "measures": []
                }
                """;

        SonarComponent component = objectMapper.readValue(json, SonarComponent.class);

        assertTrue(component.getMeasures().isEmpty());
    }

    @Test
    void shouldFailDeserializationWhenMeasureValueIsNotNumeric() {
        String json = """
                {
                  "key": "service-key",
                  "name": "service-name",
                  "measures": [
                	{ "metric": "new_coverage", "period": { "value": "not-a-number" } }
                  ]
                }
                """;

        assertThrows(Exception.class, () -> objectMapper.readValue(json, SonarComponent.class));
    }

}
