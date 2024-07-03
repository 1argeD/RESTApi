package com.example.restapi.RestTemplateTest;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
    @Bean
    public RestTemplate restTemplate() {
      Log log = LogFactory.getLog(getClass());
        ClientHttpRequestInterceptor interceptor = (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            log.info(String.format("request to URI %s with HTTP verb '%s", request.getURI(), request.getMethod().toString()));
        return execution.execute(request, body);
        };
        return new RestTemplateBuilder()
                .additionalInterceptors(interceptor).build();
    }

}
