package de.ptb.dsi.dcc_backend.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class RestTemplateConfig {

    private final CustomProperties customProperties;

    public RestTemplateConfig(CustomProperties customProperties) {
        this.customProperties = customProperties;
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();



        requestFactory.setConnectTimeout(customProperties.getTimeoutConnect());
        requestFactory.setReadTimeout(customProperties.getTimeoutRead());

        return new RestTemplate(requestFactory);
    }
}
