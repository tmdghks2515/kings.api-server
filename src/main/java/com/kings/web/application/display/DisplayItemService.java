package com.kings.web.application.display;

import com.kings.web.domain.display.DisplayItem;
import com.kings.web.domain.display.DisplayItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisplayItemService {

    private final DisplayItemRepository displayItemRepository;

    @Transactional
    public Long create(DisplayItemCommand command) {
        validate(command);

        var displayItem = DisplayItem.create(command.type(), command.sortOrder());

        return displayItemRepository.save(displayItem).getId();
    }

    @Transactional(readOnly = true)
    public List<DisplayItemData> findAll() {
        return displayItemRepository.findAll()
                .stream()
                .map(DisplayItemData::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DisplayItemData findById(Long id) {
        return DisplayItemData.from(getById(id));
    }

    @Transactional
    public void update(Long id, DisplayItemCommand command) {
        validate(command);

        var displayItem = getById(id);
        displayItem.update(command.type(), command.sortOrder());
    }

    @Transactional
    public void delete(Long id) {
        displayItemRepository.delete(getById(id));
    }

    private DisplayItem getById(Long id) {
        return displayItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "display item not found"));
    }

    private void validate(DisplayItemCommand command) {
        if (command.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (command.sortOrder() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sortOrder must be greater than or equal to 0");
        }
    }
}
