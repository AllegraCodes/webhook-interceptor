package dev.allegra.webhookinterceptor.node;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class ProcessedNode {

    private final JsonNode originalRoot;
    private final JsonNode processedRoot;
    private final List<Entry<String, String>> replacements;

    protected ProcessedNode() {
        this.originalRoot = null;
        this.processedRoot = null;
        this.replacements = Collections.emptyList();
    }

    protected ProcessedNode(JsonNode originalRoot, JsonNode processedRoot, List<Entry<String, String>> replacements) {
        this.originalRoot = originalRoot;
        this.processedRoot = processedRoot;
        this.replacements = replacements != null ? replacements : Collections.emptyList();
    }

    public boolean hasReplacements() {
        return replacements != null && !replacements.isEmpty();
    }
    
}
