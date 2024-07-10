package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.work.entity.RollBackVersion;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RollBackVersionRepository extends JpaRepository<RollBackVersion, Long> {
    boolean existsByWorkAndVersionNum(Work work, Double versionNum);

    void deleteRollBackVersionByWorkAndVersionNum(Work work, Double versionNum);

    @Query("select rv from RollBackVersion rv where rv.work = :work and rv.confirm = 'COMPLETE'")
    List<RollBackVersion> findByWork(@Param("work") Work work);
}
