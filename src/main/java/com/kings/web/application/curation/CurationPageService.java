package com.kings.web.application.curation;

import com.kings.web.domain.curation.page.CurationPage;
import com.kings.web.domain.curation.page.CurationPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurationPageService {

    private final CurationPageRepository curationPageRepository;

    @Transactional(readOnly = true)
    public List<CurationPageData> findAll() {
        return curationPageRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CurationPage::getId))
                .map(CurationPageData::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CurationPageDetailData findById(Long id) {
        return curationPageRepository.findById(id)
                .map(CurationPageDetailData::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "curation page not found"));
    }
}
