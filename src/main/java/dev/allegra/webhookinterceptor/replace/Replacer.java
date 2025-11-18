package dev.allegra.webhookinterceptor.replace;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableConfigurationProperties(ReplacerProperties.class)
@Service
public class Replacer {

    private final ReplacerProperties properties;

    public boolean shouldReplace(String str) {
        return str != null && str.matches(properties.getMatcher());
    }

    public String getReplacement(int index) {
        if (properties.isIndex()) {
            return properties.getPrefix() + properties.getReplacement() + index + properties.getSuffix();
        } else {
            return properties.getReplacement();
        }
    }

}
