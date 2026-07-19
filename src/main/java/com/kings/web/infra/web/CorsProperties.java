package com.kings.web.infra.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "web.cors")
public class CorsProperties {

    private List<String> allowedOrigins = List.of(
            "http://localhost:3001",
            "http://localhost:3002"
    );
}
