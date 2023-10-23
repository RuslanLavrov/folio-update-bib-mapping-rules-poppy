package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class MappingRulesUtil {

    public static Map<String, JsonNode> getChangedMappingRules(JsonNode originalMappingRules, JsonNode actualMappingRules) {
        Map<String, JsonNode> changedRules = new LinkedHashMap<>();
        originalMappingRules.fields().forEachRemaining(marcFieldRule -> {
            JsonNode actualMarcFieldRule = actualMappingRules.get(marcFieldRule.getKey());
            if (!marcFieldRule.getValue().equals(actualMarcFieldRule)) {
                changedRules.put(marcFieldRule.getKey(), actualMarcFieldRule);
            }
        });
        return changedRules;
    }

    public static void replaceMappingRulesForMarcFields(ObjectNode rulesReplacement, ObjectNode targetMappingRules) {
        rulesReplacement.fields().forEachRemaining(marcFieldRule -> {
            targetMappingRules.set(marcFieldRule.getKey(), marcFieldRule.getValue());
            log.info("Mapping rules for MARC field \"{}\" have been updated", marcFieldRule.getKey());
        });
    }

}
