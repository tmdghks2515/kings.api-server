package com.kings.web.domain.file;

import com.kings.web.domain.audit.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Entity
@Table(name = "file_resource")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileResource extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 255)
    private String originalName;

    @Column(nullable = false, unique = true, length = 500)
    private String storageKey;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(length = 20)
    private String extension;

    @Column(nullable = false)
    private long sizeBytes;

    private FileResource(
            String originalName,
            String storageKey,
            String contentType,
            String extension,
            long sizeBytes
    ) {
        this.originalName = Objects.requireNonNull(originalName, "원본 파일명은 필수입니다.");
        this.storageKey = Objects.requireNonNull(storageKey, "파일 저장 키는 필수입니다.");
        this.contentType = Objects.requireNonNull(contentType, "파일 콘텐츠 타입은 필수입니다.");
        this.extension = extension;
        this.sizeBytes = validateSize(sizeBytes);
    }

    public static FileResource register(
            String originalName,
            String storageKey,
            String contentType,
            String extension,
            long sizeBytes
    ) {
        return new FileResource(originalName, storageKey, contentType, extension, sizeBytes);
    }

    private long validateSize(long sizeBytes) {
        if (sizeBytes <= 0) {
            throw new IllegalArgumentException("파일 크기는 0보다 커야 합니다.");
        }
        return sizeBytes;
    }
}
