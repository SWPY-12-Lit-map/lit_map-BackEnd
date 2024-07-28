package com.lit_map_BackEnd.domain.relation.service;

import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Work;

import java.util.List;

public interface RelationService{

    //List<Work> recommendRelatedWorks(Work searchedWork);
    //List<Work> recommendRelatedWorksById(Long workId);
  //  List<WorkResponseDto> recommendRelatedWorks(Work searchedWork);
 //   List<WorkResponseDto> recommendRelatedWorksById(Long workId);

    List<Work> findRelatedWorks(Long workId);
}
