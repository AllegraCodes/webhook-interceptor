package dev.allegra.webhookinterceptor.replace;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("replacer")
public class ReplacerProperties {

    private String matcher;
    private String replacement;
    private boolean index;
    private String prefix;
    private String suffix;

}
