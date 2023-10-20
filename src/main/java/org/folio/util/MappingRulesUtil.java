package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

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
}
