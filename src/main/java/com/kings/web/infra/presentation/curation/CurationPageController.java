package com.kings.web.infra.presentation.curation;

import com.kings.web.application.curation.CurationPageData;
import com.kings.web.application.curation.CurationPageDetailData;
import com.kings.web.application.curation.CurationPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/curation-pages")
@RequiredArgsConstructor
public class CurationPageController {

    private final CurationPageService curationPageService;

    @GetMapping
    public List<CurationPageData> findAll() {
        return curationPageService.findAll();
    }

    @GetMapping("/{id}")
    public CurationPageDetailData findById(@PathVariable Long id) {
        return curationPageService.findById(id);
    }
}
