package com.kings.web.infra.data.jpa.curation;

import com.kings.web.domain.curation.Curation;
import com.kings.web.domain.curation.CurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CurationRepositoryImpl implements CurationRepository {

    private final CurationJpaRepository curationJpaRepository;

    @Override
    public Curation save(Curation curation) {
        return curationJpaRepository.save(curation);
    }

    @Override
    public List<Curation> findAll() {
        return curationJpaRepository.findAll();
    }

    @Override
    public Optional<Curation> findById(Long id) {
        return curationJpaRepository.findById(id);
    }

    @Override
    public void delete(Curation curation) {
        curationJpaRepository.delete(curation);
    }
}
