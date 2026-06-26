package com.kings.web.domain.file;

import java.util.List;
import java.util.Optional;

public interface FileResourceRepository {

    FileResource save(FileResource fileResource);

    Optional<FileResource> findById(Long id);

    Optional<FileResource> findByStorageKey(String storageKey);

    boolean existsByStorageKey(String storageKey);

    void deleteAll(List<FileResource> fileResources);
}
