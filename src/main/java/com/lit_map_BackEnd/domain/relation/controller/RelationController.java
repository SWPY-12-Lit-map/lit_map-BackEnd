package com.lit_map_BackEnd.domain.relation.controller;

import com.lit_map_BackEnd.domain.relation.service.RelationService;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relate")
public class RelationController {

    @Autowired
    private RelationService relationService;

/*    @PostMapping("/recommend")
    public ResponseEntity<List<Work>> recommendRelatedWorks(@RequestBody Work searchedWork) {
        List<Work> recommendedWorks = relationService.recommendRelatedWorks(searchedWork);
        return ResponseEntity.ok(recommendedWorks);
    }

    @PostMapping("/recommendID")
    public ResponseEntity<List<Work>> recommendRelatedWorks(@RequestBody Long workId) {
        List<Work> recommendedWorks = relationService.recommendRelatedWorksById(workId);
        return ResponseEntity.ok(recommendedWorks);
    }


@PostMapping("/recommendID2")
    public ResponseEntity<List<WorkResponseDto>> recommendRelatedWorks(@RequestBody Long workId) {
        List<WorkResponseDto> recommendedWorks = relationService.recommendRelatedWorksById(workId);
        return ResponseEntity.ok(recommendedWorks);
    }

    @GetMapping("/{workId}/related-works")
    public ResponseEntity<List<WorkDTO>> findRelatedWorks(@PathVariable Long workId) {
        List<WorkDTO> relatedWorks = relationService.findRelatedWorks(workId);
        return ResponseEntity.ok(relatedWorks);
    }
  */

    @GetMapping("/related/{workId}")
    public List<Work> getRelatedWorks(@PathVariable Long workId) {
        return relationService.findRelatedWorks(workId);
    }



}


