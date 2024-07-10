package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface VersionRepository extends JpaRepository<Version, Long> {
    Version findByVersionNumAndWork(Double version, Work work);

    boolean existsByVersionNumAndWork(Double version, Work work);

    @Query("select v from Version v where v.work = :work and v.confirm = 'COMPLETE'")
    List<Version> findByWork(@Param("work")Work work);

    void deleteByWorkAndVersionNum(Work work, Double versionNum);
}
