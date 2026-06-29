package com.kings.web.domain.category;

import com.kings.web.domain.audit.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Entity
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private int depth;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    private Category(int depth, String name, Category parentCategory) {
        this.depth = depth;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.parentCategory = parentCategory;
    }

    public static Category create(int depth, String name, Category parentCategory) {
        return new Category(depth, name, parentCategory);
    }

    public void update(int depth, String name, Category parentCategory) {
        this.depth = depth;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.parentCategory = parentCategory;
    }
}
