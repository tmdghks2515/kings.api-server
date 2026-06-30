package com.kings.web.infra.data.jpa.file;

import com.kings.web.domain.file.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface FileResourceJpaRepository extends JpaRepository<FileResource, Long> {

    List<FileResource> findByIdIn(List<Long> ids);

    Optional<FileResource> findByStorageKey(String storageKey);

    List<FileResource> findByStorageKeyIn(List<String> storageKeys);

    boolean existsByStorageKey(String storageKey);
}
