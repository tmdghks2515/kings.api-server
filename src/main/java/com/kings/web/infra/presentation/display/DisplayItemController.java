package com.kings.web.infra.presentation.display;

import com.kings.web.application.display.DisplayItemCommand;
import com.kings.web.application.display.DisplayItemData;
import com.kings.web.application.display.DisplayItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/display-items")
@RequiredArgsConstructor
public class DisplayItemController {

    private final DisplayItemService displayItemService;

    @PostMapping
    public Long create(@RequestBody DisplayItemCommand command) {
        return displayItemService.create(command);
    }

    @GetMapping
    public List<DisplayItemData> findAll() {
        return displayItemService.findAll();
    }

    @GetMapping("/{id}")
    public DisplayItemData findById(@PathVariable Long id) {
        return displayItemService.findById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody DisplayItemCommand command) {
        displayItemService.update(id, command);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        displayItemService.delete(id);
    }
}
