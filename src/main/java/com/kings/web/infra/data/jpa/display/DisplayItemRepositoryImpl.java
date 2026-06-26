package com.kings.web.infra.data.jpa.display;

import com.kings.web.domain.display.DisplayItem;
import com.kings.web.domain.display.DisplayItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DisplayItemRepositoryImpl implements DisplayItemRepository {

    private final DisplayItemJpaRepository displayItemJpaRepository;

    @Override
    public DisplayItem save(DisplayItem displayItem) {
        return displayItemJpaRepository.save(displayItem);
    }

    @Override
    public List<DisplayItem> findAll() {
        return displayItemJpaRepository.findAll();
    }

    @Override
    public Optional<DisplayItem> findById(Long id) {
        return displayItemJpaRepository.findById(id);
    }

    @Override
    public void delete(DisplayItem displayItem) {
        displayItemJpaRepository.delete(displayItem);
    }
}
