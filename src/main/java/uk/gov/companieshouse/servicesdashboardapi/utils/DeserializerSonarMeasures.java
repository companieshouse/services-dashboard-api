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

   @Override
   public Map<String, Integer> deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

      Map<String, Integer> measuresMap = new HashMap<>();
      JsonNode node = jp.getCodec().readTree(jp);

      for (JsonNode element : node) {
         JsonNode metric = element.get("metric");
         JsonNode value = element.get("value");
         if (metric != null && value != null) {
            measuresMap.put(metric.asText(), Math.round(Float.parseFloat(value.asText())));
         }
      }
      return measuresMap;
   }
}
