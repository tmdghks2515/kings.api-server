package com.kings.web.application.file;

import com.kings.web.domain.file.FileResource;

public record FileResourceData(
        Long id,
        String originalName,
        String storageKey,
        String contentType,
        String extension,
        long sizeBytes
) {
    public static FileResourceData from(FileResource fileResource) {
        return new FileResourceData(
                fileResource.getId(),
                fileResource.getOriginalName(),
                fileResource.getStorageKey(),
                fileResource.getContentType(),
                fileResource.getExtension(),
                fileResource.getSizeBytes()
        );
    }
}
