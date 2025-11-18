package dev.allegra.webhookinterceptor.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.allegra.webhookinterceptor.replace.Replacer;

@JsonTest
@Import(NodeProcessor.class)
class NodeProcessorTests {

    @Autowired
    NodeProcessor processor;

    @MockitoBean
    ObjectMapper mockMapper;

    @MockitoBean
    Replacer mockReplacer;

    @Test
    void nullRoot() {
        var result = processor.process(null);

        assertNull(result.getOriginalRoot());
        assertNull(result.getProcessedRoot());
        assertEquals(Collections.emptyList(), result.getReplacements());
    }

    @Test
    void emptyObject() {
        var mockOriginalNode = mock(ObjectNode.class);
        when(mockOriginalNode.getNodeType()).thenReturn(JsonNodeType.OBJECT);
        var mockProcessedNode = mock(ObjectNode.class);
        when(mockMapper.createObjectNode()).thenReturn(mockProcessedNode);

        var result = processor.process(mockOriginalNode);

        assertSame(mockOriginalNode, result.getOriginalRoot());
        assertSame(mockProcessedNode, result.getProcessedRoot());
        assertEquals(Collections.emptyList(), result.getReplacements());
    }

    @SuppressWarnings("unchecked")
    @Test
    void emptyArray() {
        var mockOriginalNode = mock(ArrayNode.class);
        when(mockOriginalNode.getNodeType()).thenReturn(JsonNodeType.ARRAY);
        var mockIterator = mock(Iterator.class);
        when(mockOriginalNode.iterator()).thenReturn(mockIterator);
        when(mockIterator.hasNext()).thenReturn(false);
        var mockProcessedNode = mock(ArrayNode.class);
        when(mockMapper.createArrayNode()).thenReturn(mockProcessedNode);

        var result = processor.process(mockOriginalNode);

        assertSame(mockOriginalNode, result.getOriginalRoot());
        assertSame(mockProcessedNode, result.getProcessedRoot());
        assertEquals(Collections.emptyList(), result.getReplacements());
    }

}
