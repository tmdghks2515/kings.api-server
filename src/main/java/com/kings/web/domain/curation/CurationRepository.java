package com.kings.web.domain.curation;

import java.util.List;
import java.util.Optional;

public interface CurationRepository {
    Curation save(Curation curation);

    List<Curation> findAll();

    Optional<Curation> findById(Long id);

    void delete(Curation curation);
}
