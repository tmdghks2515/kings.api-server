package com.kings.web.infra.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageProperties {

    private String path;

    public Path rootPath() {
        return Path.of(path).toAbsolutePath().normalize();
    }
}
