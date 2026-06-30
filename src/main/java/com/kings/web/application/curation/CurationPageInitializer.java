package com.kings.web.application.curation;

import com.kings.web.domain.curation.page.CurationPage;
import com.kings.web.domain.curation.page.CurationPageRepository;
import com.kings.web.domain.curation.page.CurationPageType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurationPageInitializer {

    private final CurationPageRepository curationPageRepository;

    @PostConstruct
    void initialize() {
        var mainPage = curationPageRepository.findByType(CurationPageType.MAIN);

        if (mainPage.isEmpty()) {
            curationPageRepository.save(CurationPage.create(CurationPageType.MAIN));
        }
    }
}
