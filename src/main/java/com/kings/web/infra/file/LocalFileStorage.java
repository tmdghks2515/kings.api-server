package com.kings.web.infra.file;

import com.kings.web.application.file.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;

@Component
@RequiredArgsConstructor
public class LocalFileStorage implements FileStorage {

    private final FileStorageProperties properties;

    @Override
    public void store(MultipartFile file, String storageKey) {
        var targetPath = resolveTargetPath(storageKey);

        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "failed to store file", e);
        }
    }

    @Override
    public void delete(String storageKey) {
        var targetPath = resolveTargetPath(storageKey);

        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "failed to delete file", e);
        }
    }

    private java.nio.file.Path resolveTargetPath(String storageKey) {
        var rootPath = properties.rootPath();
        var targetPath = rootPath.resolve(storageKey).normalize();

        if (!targetPath.startsWith(rootPath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid storage path");
        }

        return targetPath;
    }
}
