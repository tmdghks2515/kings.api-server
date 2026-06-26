package com.kings.web.domain.display;

import java.util.List;
import java.util.Optional;

public interface DisplayItemRepository {
    DisplayItem save(DisplayItem displayItem);

    List<DisplayItem> findAll();

    Optional<DisplayItem> findById(Long id);

    void delete(DisplayItem displayItem);
}
