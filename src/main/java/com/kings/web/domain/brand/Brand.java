package com.kings.web.domain.brand;

import com.kings.web.domain.audit.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Entity
@Table(name = "brand")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(length = 1000)
    private String introduce;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "logo_storage_key", length = 500)
    private String logoStorageKey;

    @Column(name = "main_image_storage_key", length = 500)
    private String mainImageStorageKey;

    private Brand(String name, String introduce, int sortOrder, String logoStorageKey, String mainImageStorageKey) {
        this.name = Objects.requireNonNull(name, "브랜드명은 필수입니다.");
        this.introduce = introduce;
        this.sortOrder = sortOrder;
        this.logoStorageKey = logoStorageKey;
        this.mainImageStorageKey = mainImageStorageKey;
    }

    public static Brand create(String name, String introduce, int sortOrder, String logoStorageKey, String mainImageStorageKey) {
        return new Brand(name, introduce, sortOrder, logoStorageKey, mainImageStorageKey);
    }

    public void update(String name, String introduce, int sortOrder, String logoStorageKey, String mainImageStorageKey) {
        this.name = Objects.requireNonNull(name, "브랜드명은 필수입니다.");
        this.introduce = introduce;
        this.sortOrder = sortOrder;
        this.logoStorageKey = logoStorageKey;
        this.mainImageStorageKey = mainImageStorageKey;
    }
}
