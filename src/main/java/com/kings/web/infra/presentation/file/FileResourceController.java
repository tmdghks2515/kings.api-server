package com.kings.web.infra.presentation.file;

import com.kings.web.application.file.FileRegisterService;
import com.kings.web.application.file.FileResourceData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileResourceController {

    private final FileRegisterService fileRegisterService;

    @PostMapping
    public FileResourceData register(@RequestPart("file") MultipartFile file) {
        return fileRegisterService.register(file);
    }
}
