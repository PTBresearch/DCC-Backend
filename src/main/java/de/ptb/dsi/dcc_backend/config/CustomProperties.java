package de.ptb.dsi.dcc_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom")
@Data
public class CustomProperties {

    private String proxyHost;
    private int proxyPort;
    private int timeoutConnect;
    private int timeoutRead;

}
