package com.kings.web.application.file;

import com.kings.web.domain.file.FileResource;
import com.kings.web.domain.file.FileResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileRegisterService {

    private static final DateTimeFormatter STORAGE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final FileResourceRepository fileResourceRepository;
    private final FileStorage fileStorage;

    @Transactional
    public FileResourceData register(MultipartFile file) {
        validate(file);

        var originalName = cleanOriginalName(file.getOriginalFilename());
        var extension = extractExtension(originalName);
        var storageKey = createStorageKey(extension);
        var contentType = StringUtils.hasText(file.getContentType())
                ? file.getContentType()
                : "application/octet-stream";

        fileStorage.store(file, storageKey);

        var fileResource = FileResource.register(
                originalName,
                storageKey,
                contentType,
                extension,
                file.getSize()
        );

        return FileResourceData.from(fileResourceRepository.save(fileResource));
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file is required");
        }
    }

    private String cleanOriginalName(String originalFilename) {
        var filename = StringUtils.cleanPath(
                StringUtils.hasText(originalFilename) ? originalFilename : "file"
        );

        if (filename.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid filename");
        }
        return filename;
    }

    private String extractExtension(String filename) {
        var extension = StringUtils.getFilenameExtension(filename);
        return StringUtils.hasText(extension) ? extension.toLowerCase(Locale.ROOT) : null;
    }

    private String createStorageKey(String extension) {
        var filename = UUID.randomUUID().toString();
        if (StringUtils.hasText(extension)) {
            filename += "." + extension;
        }
        return LocalDate.now().format(STORAGE_DATE_FORMAT) + "/" + filename;
    }
}
