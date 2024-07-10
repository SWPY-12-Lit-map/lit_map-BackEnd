package com.lit_map_BackEnd.domain.work.repository;

import com.lit_map_BackEnd.domain.work.entity.RollBackVersion;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RollBackVersionRepository extends JpaRepository<RollBackVersion, Long> {
    boolean existsByWorkAndVersionNum(Work work, Double versionNum);

    void deleteRollBackVersionByWorkAndVersionNum(Work work, Double versionNum);
}
