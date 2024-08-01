package com.lit_map_BackEnd.domain.relation.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.relation.dto.RelationDto;
import com.lit_map_BackEnd.domain.relation.repository.RelationRepository;

import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationServiceImpl implements RelationService {

    private final RelationRepository relationRepository;
    private final WorkRepository workRepository;


    public List<RelationDto> findRelatedWorks(Long workId) {
        Work baseWork = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        List<Work> relatedWorksByAuthor = relationRepository.findOtherWorksWithSameCategoryGenreSortedByAuthor(workId);
        List<Work> relatedWorksByGenre = relationRepository.findWorksWithSameCategoryAndAuthorButDifferentGenre(workId);

        relatedWorksByAuthor.addAll(relatedWorksByGenre);

        List<RelationDto> relatedWorkDTOs = relatedWorksByAuthor.stream()
                .map(work -> {
                    RelationDto dto = new RelationDto(work.getId(), work.getTitle(), work.getImageUrl());
                    dto.setWorkId(work.getId());
                    dto.setTitle(work.getTitle());
                    dto.setImageUrl(work.getImageUrl());
                    return dto;
                })
                .collect(Collectors.toList());

        System.out.println(relatedWorkDTOs);
        return relatedWorkDTOs;
    }


}
