package com.kings.web.infra.presentation.curation;

import com.kings.web.application.curation.CurationPageService;
import com.kings.web.application.curation.PublicCurationPageData;
import com.kings.web.domain.curation.page.CurationPageType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/curation-pages")
@RequiredArgsConstructor
public class PublicCurationPageController {

    private final CurationPageService curationPageService;

    @GetMapping("/{type}")
    public PublicCurationPageData findByType(@PathVariable CurationPageType type) {
        return curationPageService.findPublicByType(type);
    }
}
