package org.folio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.folio.client.AuthClient;
import org.folio.client.SRMClient;
import org.folio.model.Configuration;
import org.folio.util.FileWorker;
import org.folio.util.HttpWorker;
import org.folio.util.MappingRulesUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.folio.FolioMappingRulesUpdateApp.exitWithError;
import static org.folio.FolioMappingRulesUpdateApp.exitWithMessage;

@Slf4j
@Service
public class UpdateMappingRulesService {

    private static final String ORCHID_MAPPING_RULES_PATH = "mappingRules/orchidMappingRules/";
    private static final String POPPY_MAPPING_RULES_PATH = "mappingRules/poppyMappingRules/";
    public static final String MAPPING_RULES_CHANGED_ERROR_MSG = """
        Failed to update exising mapping rules for the 'classification' field because they were changed/customized for the following MARC fields: %s.
        The mapping for the 'classification' field should be updated manually by following this guide:
        https://wiki.folio.org/display/FOLIJET/Update+of+mapping+to+correct+handling+of+repeated+classification+fields+and+subfields
        """;
    private MappingRulesUtil mappingRulesUtil;
    private Configuration configuration;
    private SRMClient srmClient;
    private String MARC_BIB = "marc-bib";

    public void start() {
        configuration = FileWorker.getConfiguration();
        var httpWorker = new HttpWorker(configuration);
        var authClient = new AuthClient(configuration, httpWorker);
        
        httpWorker.setOkapiToken(authClient.authorize());

        srmClient = new SRMClient(httpWorker);
        mappingRulesUtil = new MappingRulesUtil();

        updateMappingRules();

        exitWithMessage("Script execution completed");
    }

    private void updateMappingRules() {
        JsonNode existingMappingRules = srmClient.retrieveMappingRules(MARC_BIB);

        ObjectNode orchidClassificationRules = FileWorker.getJsonObject(ORCHID_MAPPING_RULES_PATH + "classificationFieldMappingRules.json");
        Map<String, JsonNode> changedMappingRules = MappingRulesUtil.getChangedMappingRules(orchidClassificationRules, existingMappingRules);
        if (!changedMappingRules.isEmpty()) {
            String msg = String.format(MAPPING_RULES_CHANGED_ERROR_MSG, changedMappingRules.keySet());
            exitWithError(msg);
        }
    }

}
