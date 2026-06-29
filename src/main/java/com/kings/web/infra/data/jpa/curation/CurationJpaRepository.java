package com.kings.web.infra.data.jpa.curation;

import com.kings.web.domain.curation.Curation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurationJpaRepository extends JpaRepository<Curation, Long> {
}
