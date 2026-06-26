package com.kings.web.infra.data.jpa.display;

import com.kings.web.domain.display.DisplayItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayItemJpaRepository extends JpaRepository<DisplayItem, Long> {
}
