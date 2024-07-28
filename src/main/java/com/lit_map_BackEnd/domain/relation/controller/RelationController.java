package com.lit_map_BackEnd.domain.relation.controller;

import com.lit_map_BackEnd.domain.relation.dto.RelationDto;
import com.lit_map_BackEnd.domain.relation.service.RelationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relate")
public class RelationController {

    @Autowired
    private RelationService relationService;

    //테스트용
    @GetMapping("/related/{workId}")
    @Operation(summary = "연관 작품 추천 (테스트)", description = "연관 작품 추천 (테스트)")
    public List<RelationDto> getRelatedWorks(@PathVariable Long workId) {
        return relationService.findRelatedWorks(workId);
    }



}


