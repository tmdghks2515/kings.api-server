package com.kings.web.domain.display;

import com.kings.web.domain.audit.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "display_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DisplayItem extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DisplayItemType type;

    @Column(name = "sort_order", nullable = false)
    private int order;

    private DisplayItem(String name, DisplayItemType type, int order) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.order = order;
    }

    public static DisplayItem create(String name, DisplayItemType type, int order) {
        return new DisplayItem(name, type, order);
    }

    public void update(String name, DisplayItemType type, int order) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.order = order;
    }
}
