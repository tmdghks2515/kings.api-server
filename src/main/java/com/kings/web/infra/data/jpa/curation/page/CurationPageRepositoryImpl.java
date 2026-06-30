package com.kings.web.infra.data.jpa.curation.page;

import com.kings.web.domain.curation.page.CurationPage;
import com.kings.web.domain.curation.page.CurationPageRepository;
import com.kings.web.domain.curation.page.CurationPageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CurationPageRepositoryImpl implements CurationPageRepository {

    private final CurationPageJpaRepository curationPageJpaRepository;

    @Override
    public CurationPage save(CurationPage curationPage) {
        return curationPageJpaRepository.save(curationPage);
    }

    @Override
    public List<CurationPage> findAll() {
        return curationPageJpaRepository.findAll();
    }

    @Override
    public Optional<CurationPage> findById(Long id) {
        return curationPageJpaRepository.findById(id);
    }

    @Override
    public Optional<CurationPage> findByType(CurationPageType type) {
        return curationPageJpaRepository.findByType(type);
    }
}
