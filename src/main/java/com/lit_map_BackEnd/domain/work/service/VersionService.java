package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.domain.mail.dto.MailWorkDto;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface VersionService {

    // 기존에 존재하는 상황이면 여기서 수정한다.
    Version changeVersion(Double versionNum, String versionName, Map<String, Object> relationship, Work work);

    VersionResponseDto findVersionByWorkAndNumber(Long workId, Double versionNum);

    void deleteVersion(Member member, Long workId, Double versionNum);

    List<VersionListDto> versionList(Work work);

    void rollBackDataSave(Long memberId, Long workId, Double versionNum);
   //    void rollBackDataSave(Long workId, Double versionNum);


    MailWorkDto sendMailWithTemplate(Long versionId, String subject, String content);

    void approveMail(Long versionId, HttpServletRequest request);

    void declineMail(Long versionId, String reason, HttpServletRequest request);


}
