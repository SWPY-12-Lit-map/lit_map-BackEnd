package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface VersionRepository extends JpaRepository<Version, Long> {
    Version findByVersionNumAndWork(Double version, Work work);

    boolean existsByVersionNumAndWork(Double version, Work work);

    @Query("select v from Version v where v.work = :work and v.confirm = 'COMPLETE'")
    List<Version> findByWorkComplete(@Param("work")Work work);

    @Query("select v from Version v where v.work = :work and v.confirm = 'CONFIRM'")
    List<Version> findByWorkConfirm(@Param("work")Work work);

    void deleteByWorkAndVersionNum(Work work, Double versionNum);

    @Query(value = "SELECT work_id, MAX(updated_date) as max_updated_date " +
            "FROM (" +
            "  SELECT work_id, updated_date " +
            "  FROM work_version " +
            "  WHERE confirm = 'COMPLETE' " +
            "  UNION ALL " +
            "  SELECT work_id, updated_date " +
            "  FROM rollback_versions " +
            "  WHERE confirm = 'COMPLETE' " +
            ") combined " +
            "GROUP BY work_id " +
            "ORDER BY max_updated_date DESC "
            , nativeQuery = true)
    Slice<Object[]> findLatestUpdateDates(Pageable pageable);
}
