package com.lit_map_BackEnd.domain.board.dto;

import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ConfirmListDto {
    // 작품 이름
    String workTitle;
    // 그 안에 승인 필요한 버전 정보는 리스트로 가져오기
    List<String> versionList;
}
