package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class MappingRulesUtilTest {

    @Test
    public void shouldReturnDifferentMappingRules() {
        ObjectNode rulesToCompare = FileWorker.getJsonObject("rules/poppyClassificationFieldMappingRules.json");
        ObjectNode mappingRulesWithChanges = FileWorker.getJsonObject("rules/rules_with_differences_in_050_060_082.json");

        Map<String, JsonNode> differentRules = MappingRulesUtil.getChangedMappingRules(rulesToCompare, mappingRulesWithChanges);

        MatcherAssert.assertThat(differentRules.keySet(), Matchers.contains("050", "060", "082"));
    }

    @Test
    public void shouldReplaceSpecifiedMappingRules() {
        ObjectNode rulesReplacement = FileWorker.getJsonObject("rules/poppyClassificationFieldMappingRules.json");
        ObjectNode mappingRulesWithCustomizations = FileWorker.getJsonObject("rules/rules_with_differences_in_050_060_082.json");
        ObjectNode expectedUpdatedRules = FileWorker.getJsonObject("rules/expectedUpdatedMappingRules.json");

        MappingRulesUtil.replaceMappingRulesForMarcFields(rulesReplacement, mappingRulesWithCustomizations);
        Assert.assertEquals(expectedUpdatedRules, mappingRulesWithCustomizations);
    }

    @Test
    public void shouldReturnMappingRulesThatAreNotRelatedToSpecifiedMarcFields() {
        // rulesToFilter contains rules for these fields: 050, 060, 082, 090
        ObjectNode rulesToFilter = FileWorker.getJsonObject("rules/poppyClassificationFieldMappingRules.json");
        ObjectNode filteredMappingRules = MappingRulesUtil.getMappingRulesExcludingByMarcFields(rulesToFilter, Set.of("082", "090"));
        List<String> filteredMappingRulesMarcFields = StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(filteredMappingRules.fieldNames(), Spliterator.ORDERED), false)
            .toList();

        Assert.assertEquals(2, filteredMappingRules.size());
        MatcherAssert.assertThat(filteredMappingRulesMarcFields, Matchers.contains("050", "060"));
        MatcherAssert.assertThat(filteredMappingRulesMarcFields, Matchers.not(Matchers.contains("082", "090")));
    }

}