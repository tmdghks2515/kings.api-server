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
        this.curationPage = Objects.requireNonNull(curationPage, "큐레이션 페이지는 필수입니다.");
        this.type = Objects.requireNonNull(type, "큐레이션 타입은 필수입니다.");
        this.name = Objects.requireNonNull(name, "큐레이션명은 필수입니다.");
        this.sortOrder = sortOrder;
        this.detail = Objects.requireNonNull(detail, "큐레이션 상세 정보는 필수입니다.");
    }

    public static Curation create(CurationPage curationPage, CurationType type, String name, int sortOrder, CurationDetail detail) {
        return new Curation(curationPage, type, name, sortOrder, detail);
    }

    public void update(CurationPage curationPage, CurationType type, String name, int sortOrder, CurationDetail detail) {
        this.curationPage = Objects.requireNonNull(curationPage, "큐레이션 페이지는 필수입니다.");
        this.type = Objects.requireNonNull(type, "큐레이션 타입은 필수입니다.");
        this.name = Objects.requireNonNull(name, "큐레이션명은 필수입니다.");
        this.sortOrder = sortOrder;
        this.detail = Objects.requireNonNull(detail, "큐레이션 상세 정보는 필수입니다.");
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
