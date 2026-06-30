package com.kings.web.infra.data.jpa.curation.page;

import com.kings.web.domain.curation.page.CurationPage;
import com.kings.web.domain.curation.page.CurationPageType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurationPageJpaRepository extends JpaRepository<CurationPage, Long> {
    @Override
    @EntityGraph(attributePaths = "curations")
    List<CurationPage> findAll();

    @Override
    @EntityGraph(attributePaths = "curations")
    Optional<CurationPage> findById(Long id);

    Optional<CurationPage> findByType(CurationPageType type);
}
