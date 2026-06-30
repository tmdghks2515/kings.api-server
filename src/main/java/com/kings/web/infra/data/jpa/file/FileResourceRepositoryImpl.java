package com.kings.web.infra.data.jpa.file;

import com.kings.web.domain.file.FileResource;
import com.kings.web.domain.file.FileResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FileResourceRepositoryImpl implements FileResourceRepository {

    private final FileResourceJpaRepository fileResourceJpaRepository;

    @Override
    public FileResource save(FileResource fileResource) {
        return fileResourceJpaRepository.save(fileResource);
    }

    @Override
    public Optional<FileResource> findById(Long id) {
        return fileResourceJpaRepository.findById(id);
    }

    @Override
    public List<FileResource> findAllByIdIn(List<Long> ids) {
        return fileResourceJpaRepository.findByIdIn(ids);
    }

    @Override
    public Optional<FileResource> findByStorageKey(String storageKey) {
        return fileResourceJpaRepository.findByStorageKey(storageKey);
    }

    @Override
    public List<FileResource> findAllByStorageKeyIn(List<String> storageKeys) {
        return fileResourceJpaRepository.findByStorageKeyIn(storageKeys);
    }

    @Override
    public boolean existsByStorageKey(String storageKey) {
        return fileResourceJpaRepository.existsByStorageKey(storageKey);
    }

    @Override
    public void deleteAll(List<FileResource> fileResources) {
        fileResourceJpaRepository.deleteAll(fileResources);
    }
}
