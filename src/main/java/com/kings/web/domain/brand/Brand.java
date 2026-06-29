package com.kings.web.domain.brand;

import com.kings.web.domain.audit.BaseAuditableEntity;
import com.kings.web.domain.file.FileResource;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logo_resource_id")
    private FileResource logo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_image_resource_id")
    private FileResource mainImage;

    private Brand(String name, String introduce, FileResource logo, FileResource mainImage) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.introduce = introduce;
        this.logo = logo;
        this.mainImage = mainImage;
    }

    public static Brand create(String name, String introduce, FileResource logo, FileResource mainImage) {
        return new Brand(name, introduce, logo, mainImage);
    }

    public void update(String name, String introduce, FileResource logo, FileResource mainImage) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.introduce = introduce;
        this.logo = logo;
        this.mainImage = mainImage;
    }
}
