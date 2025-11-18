package dev.allegra.webhookinterceptor.attach;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;

import dev.allegra.webhookinterceptor.node.ProcessedNode;
import dev.allegra.webhookinterceptor.replace.ReplacerProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableConfigurationProperties({AttacherProperties.class, ReplacerProperties.class})
@Service
public class DiscordAttacher {

    private final RestClient senderClient;
    private final RestClient fetcherClient;
    private final String params;
    private final String replacement;
    private final String suffix;
    private final boolean index;

    public DiscordAttacher(RestClient.Builder clientBuiler, AttacherProperties attacherProperties, ReplacerProperties replacerProperties) {
        String webhookUrl = attacherProperties.getUrl();
        if (webhookUrl == null) {
            throw new IllegalArgumentException("WEBHOOK_URL environment variable not set");
        }
        this.senderClient = clientBuiler.baseUrl(webhookUrl).defaultStatusHandler(response -> false).build();
        this.fetcherClient = clientBuiler.build();
        this.params = attacherProperties.getParams();
        this.replacement = replacerProperties.getReplacement();
        this.suffix = replacerProperties.getSuffix();
        this.index = replacerProperties.isIndex();
    }

    public ResponseEntity<String> sendAttached(ProcessedNode processedNode) {
        JsonNode payload = processedNode.getProcessedRoot();
        if (payload == null) {
            log.warn("JSON data is null, not sending message");
            return ResponseEntity.badRequest().body("JSON data was null, message not sent");
        }

        List<String> fileUrls = processedNode.getReplacements().stream().map(Entry::getKey).toList();
        List<byte[]> files = getFiles(fileUrls);
        if (files.isEmpty()) {
            log.info("Sending without attachments");
            return senderClient.post()
                .body(payload)
                .retrieve()
                .toEntity(String.class);
        }

        MultiValueMap<String, Object> body = buildBody(payload, files);
        log.info("Sending with attachments");
        return senderClient.post()
            .body(body)
            .retrieve()
            .toEntity(String.class);
    }

    private List<byte[]> getFiles(List<String> urls) {
        List<byte[]> files = new ArrayList<>();
        for (String url : urls) {
            log.debug("Fetching file from {}", url + params);
            try {
                files.add(fetcherClient.get()
                    .uri(url + params)
                    .retrieve()
                    .body(byte[].class));
            } catch (RestClientResponseException e) {
                log.warn("Error response from server", e);
                files.add(null);
            }
        }

        log.info("Got {} files", files.size());
        return files;
    }

    private @NonNull MultiValueMap<String, Object> buildBody(JsonNode payload, List<byte[]> files) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("payload_json", payload);

        int i = 0;
        for (byte[] file : files) {
            if (file != null) {
                String filename = index ? replacement + i + suffix : replacement;
                log.debug("Attaching file: {}", filename);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDispositionFormData("files[" + i + "]", filename);
                body.add("files[" + i + "]", new HttpEntity<>(file, headers));
            }
            i++;
        }

        log.debug("Built body:\n{}", body);
        return body;
    }

}
