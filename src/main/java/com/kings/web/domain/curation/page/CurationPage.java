package com.kings.web.domain.curation.page;

import com.kings.web.domain.audit.BaseAuditableEntity;
import com.kings.web.domain.curation.Curation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Entity
@Table(name = "curation_page")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurationPage extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    private CurationPageType type;

    @OrderBy("sortOrder ASC")
    @OneToMany(mappedBy = "curationPage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<Curation> curations = new ArrayList<>();

    private CurationPage(CurationPageType type) {
        this.type = Objects.requireNonNull(type, "type must not be null");
    }

    public static CurationPage create(CurationPageType type) {
        return new CurationPage(type);
    }
}
