package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VersionRepository extends JpaRepository<Version, Long> {
    Version findByVersionNumAndWork(Double version, Work work);

    boolean existsByVersionNumAndWork(Double version, Work work);
    List<Version> findByWork(Work work);
}
