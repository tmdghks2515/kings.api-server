package com.kings.web.infra.presentation.curation;

import com.kings.web.application.curation.CurationCommand;
import com.kings.web.application.curation.CurationData;
import com.kings.web.application.curation.CurationService;
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
@RequestMapping("/api/curations")
@RequiredArgsConstructor
public class CurationController {

    private final CurationService curationService;

    @PostMapping
    public Long create(@RequestBody CurationCommand command) {
        return curationService.create(command);
    }

    @GetMapping
    public List<CurationData> findAll() {
        return curationService.findAll();
    }

    @GetMapping("/{id}")
    public CurationData findById(@PathVariable Long id) {
        return curationService.findById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody CurationCommand command) {
        curationService.update(id, command);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        curationService.delete(id);
    }
}
