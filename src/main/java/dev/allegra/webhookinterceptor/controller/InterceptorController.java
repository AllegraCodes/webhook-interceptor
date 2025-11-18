package dev.allegra.webhookinterceptor.controller;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import dev.allegra.webhookinterceptor.attach.DiscordAttacher;
import dev.allegra.webhookinterceptor.node.NodeProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@Slf4j
@RestController
public class InterceptorController {

    private final NodeProcessor processor;
    private final DiscordAttacher discordAttacher;

    @PostMapping(path = "/discord", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> attachAndSend(@RequestBody JsonNode payload) {
        log.info("Received request to attach and send to discord");
        try {
            return discordAttacher.sendAttached(processor.process(payload));
        } catch (Exception e) {
            log.error("Error processing request", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/replace")
    public JsonNode echo(@RequestBody JsonNode body) {
        log.info("Received request to process json");
        return processor.process(body).getProcessedRoot();
    }

}
