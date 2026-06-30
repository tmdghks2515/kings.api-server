package com.kings.web.domain.curation.page;

import java.util.List;
import java.util.Optional;

public interface CurationPageRepository {
    CurationPage save(CurationPage curationPage);

    List<CurationPage> findAll();

    Optional<CurationPage> findById(Long id);

    Optional<CurationPage> findByType(CurationPageType type);
}
