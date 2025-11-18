package dev.allegra.webhookinterceptor.node;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import dev.allegra.webhookinterceptor.replace.Replacer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NodeProcessor {

    private final ObjectMapper objectMapper;
    private final Replacer replacer;

    public ProcessedNode process(JsonNode root) {
        if (root == null) {
            return new ProcessedNode();
        }

        List<Entry<String, String>> replacements = new ArrayList<>();
        JsonNode processedRoot = processNode(root, replacements);

        return new ProcessedNode(root, processedRoot, List.copyOf(replacements));
    }

    private JsonNode processNode(JsonNode node, List<Entry<String, String>> replacements) {
        return switch (node.getNodeType()) {
            case STRING -> processString((TextNode) node, replacements);
            case OBJECT -> processObject((ObjectNode) node, replacements);
            case ARRAY -> processArray((ArrayNode) node, replacements);
            default -> node.deepCopy();
        };
    }

    private JsonNode processString(TextNode node, List<Entry<String, String>> replacements) {
        if (replacer.shouldReplace(node.asText())) {
            String replacement = replacer.getReplacement(replacements.size());
            replacements.add(new AbstractMap.SimpleImmutableEntry<>(node.asText(), replacement));
            return objectMapper.getNodeFactory().textNode(replacement);
        } else {
            return node.deepCopy();
        }
    }

    private JsonNode processObject(ObjectNode node, List<Entry<String, String>> replacements) {
        ObjectNode nextNode = objectMapper.createObjectNode();
        for (Entry<String, JsonNode> element : node.properties()) {
            nextNode.set(element.getKey(), processNode(element.getValue(), replacements));
        }
        return nextNode;
    }

    private JsonNode processArray(ArrayNode node, List<Entry<String, String>> replacements) {
        ArrayNode nextNode = objectMapper.createArrayNode();
        for (JsonNode element : node) {
            nextNode.add(processNode(element, replacements));
        }
        return nextNode;
    }

}
