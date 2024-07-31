package com.lit_map_BackEnd.domain.work.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.entity.Confirm;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface VersionService {

    // 기존에 존재하는 상황이면 여기서 수정한다.
    Version changeVersion(Double versionNum, String versionName, Map<String, Object> relationship, Work work);

    VersionResponseDto findVersionByWorkAndNumber(Long workId, Double versionNum);

    void deleteVersion(Member member, Long workId, Double versionNum);

    List<VersionListDto> versionList(Work work);

    void rollBackDataSave(Long memberId, Long workId, Double versionNum);
    void confirmVersion(Long versionId, Authentication authentication);

}
