package dev.allegra.webhookinterceptor.attach;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("attacher")
public class AttacherProperties {

    private String url;
    private String params;

}
