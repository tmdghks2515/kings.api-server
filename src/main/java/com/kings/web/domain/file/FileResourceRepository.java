package com.kings.web.domain.file;

import java.util.List;
import java.util.Optional;

public interface FileResourceRepository {

    FileResource save(FileResource fileResource);

    Optional<FileResource> findById(Long id);

    List<FileResource> findAllByIdIn(List<Long> ids);

    Optional<FileResource> findByStorageKey(String storageKey);

    List<FileResource> findAllByStorageKeyIn(List<String> storageKeys);

    boolean existsByStorageKey(String storageKey);

    void deleteAll(List<FileResource> fileResources);
}
