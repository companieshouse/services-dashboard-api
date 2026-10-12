package uk.gov.companieshouse.servicesdashboardapi.utils;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.HashMap;
import java.util.Map;


/**
 * Custom Jackson deserializer for Sonar measures metrics.
 * Deserializes JSON array of Sonar metrics into a Map where keys are metric names and values are their corresponding
 * numeric values. Handles both direct value metrics and nested "period" values commonly found in Sonar's new_code metrics.
 * <p>Example input JSON:
 * {@snippet lang = json:
 * [
 *         {
 *             "metric": "code_smells",
 *             "value": "514",
 *             "bestValue": false
 *         },
 *         {
 *             "metric": "new_coverage",
 *             "period": {
 *                 "index": 1,
 *                 "value": "90.6",
 *                 "bestValue": false
 *             }
 *         }
 * ]
 *}
 * Output: {@snippet lang = json:{"code_smells": 514, "new_coverage": 91}}
 */
public class DeserializerSonarMeasures extends ValueDeserializer<Map<String, Integer>> {

    /**
     * Extracts the value node from a Sonar metric element.
     * Handles two value formats:
     * <ul>
     *     <li>Direct "value" field for standard metrics</li>
     *     <li>Nested "period.value" field for new_code metrics</li>
     * </ul>
     *
     * @param element the JSON node representing a single Sonar metric
     * @return the value JsonNode if found, or null if neither format is present
     */
    private JsonNode extractValueNode(JsonNode element) {
        JsonNode valueNode = element.get("value");
        if (valueNode != null && !valueNode.isNull()) {
            return valueNode;
        }

        JsonNode periodNode = element.get("period");
        if (periodNode != null) {
            return periodNode.get("value");
        }

        return null;
    }

    /**
     * Deserializes a JSON array of Sonar metrics into a map of metric names to values.
     * Iterates through each metric in the JSON array, extracting the metric name and value,
     * then converts the value from string to integer via floating-point rounding.
     *
     * @param jp the JsonParser containing the JSON to deserialize
     * @param ctxt the deserialization context
     * @return a Map containing metric names as keys and their rounded integer values as values
     * @throws JacksonException if an error occurs during JSON parsing
     */
    @Override
    public Map<String, Integer> deserialize(JsonParser jp, DeserializationContext ctxt) throws JacksonException {
        Map<String, Integer> measuresMap = new HashMap<>();
        JsonNode node = jp.objectReadContext().readTree(jp);

        for (JsonNode element : node) {
            JsonNode metric = element.get("metric");
            JsonNode value = extractValueNode(element);
            if (metric != null && value != null) {
                measuresMap.put(metric.asString(), Math.round(Float.parseFloat(value.asString())));
            }
        }
        return measuresMap;
    }
}
