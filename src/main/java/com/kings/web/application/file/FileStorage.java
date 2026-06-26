package com.kings.web.application.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    void store(MultipartFile file, String storageKey);

    void delete(String storageKey);
}
