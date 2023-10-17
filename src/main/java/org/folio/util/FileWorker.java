package org.folio.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.folio.model.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.folio.FolioMappingRulesUpdateApp.exitWithError;

@Slf4j
public class FileWorker {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static File configurationFile;

    public static Configuration getConfiguration() {
        return getMappedFile(configurationFile, Configuration.class);
    }

    public static InputStream getResourceFile(String name) {
        try {
            return ResourceUtils.getURL("classpath:" + name).openStream();
        } catch (IOException e) {
            exitWithError("Failed to read file: " + name);
            return null;
        }
    }

    public static <T> T getMappedFile(File file, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(file, clazz);
        } catch (IOException e) {
            exitWithError("Failed to map file value: " + file.getName());
            return null;
        }
    }

    public static ObjectNode getJsonObject(String name) {
        try {
            var file = getResourceFile(name);
            return (ObjectNode) OBJECT_MAPPER.readTree(file);
        } catch (IOException e) {
            exitWithError("Failed to map json file value: " + name);
            return null;
        }
    }
}
