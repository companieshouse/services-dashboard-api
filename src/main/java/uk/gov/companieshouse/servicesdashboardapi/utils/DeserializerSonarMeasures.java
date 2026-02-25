package uk.gov.companieshouse.servicesdashboardapi.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DeserializerSonarMeasures extends JsonDeserializer<Map<String, Integer>> {

   /* 
      Sonar's new_code metric values are nested within a "period" node.
      
      For example:
      {
        "metric": "code_smells",
        "value": "514",
        "bestValue": false
      },
      {
        "metric": "new_coverage",
        "period": {
          "index": 1,
          "value": "90.6",
          "bestValue": false
        }
      }
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

   @Override
   public Map<String, Integer> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

      Map<String, Integer> measuresMap = new HashMap<>();
      JsonNode node = jp.getCodec().readTree(jp);

      for (JsonNode element : node) {
         JsonNode metric = element.get("metric");
         JsonNode value = extractValueNode(element);
         if (metric != null && value != null) {
            measuresMap.put(metric.asText(), Math.round(Float.parseFloat(value.asText())));
         }
      }
      return measuresMap;
   }
}
