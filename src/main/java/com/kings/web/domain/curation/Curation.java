package com.kings.web.domain.curation;

import com.kings.web.domain.audit.BaseAuditableEntity;
import com.kings.web.domain.curation.detail.CurationDetail;
import com.kings.web.domain.curation.page.CurationPage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

@Getter
@Entity
@Table(name = "curation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Curation extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CurationType type;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    private CurationDetail detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curation_page_id", nullable = false)
    private CurationPage curationPage;

    private Curation(CurationPage curationPage, CurationType type, String name, int sortOrder, CurationDetail detail) {
        this.curationPage = Objects.requireNonNull(curationPage, "curationPage must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.sortOrder = sortOrder;
        this.detail = Objects.requireNonNull(detail, "detail must not be null");
    }

    public static Curation create(CurationPage curationPage, CurationType type, String name, int sortOrder, CurationDetail detail) {
        return new Curation(curationPage, type, name, sortOrder, detail);
    }

    public void update(CurationPage curationPage, CurationType type, String name, int sortOrder, CurationDetail detail) {
        this.curationPage = Objects.requireNonNull(curationPage, "curationPage must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.sortOrder = sortOrder;
        this.detail = Objects.requireNonNull(detail, "detail must not be null");
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
