package com.kings.web.infra.data.jpa.file;

import com.kings.web.domain.file.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileResourceJpaRepository extends JpaRepository<FileResource, Long> {

    Optional<FileResource> findByStorageKey(String storageKey);

    boolean existsByStorageKey(String storageKey);
}
