package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.character.dto.CastResponseDto;
import com.lit_map_BackEnd.domain.character.entity.Cast;
import com.lit_map_BackEnd.domain.character.entity.RollBackCast;
import com.lit_map_BackEnd.domain.character.repository.CastRepository;
import com.lit_map_BackEnd.domain.character.service.CastService;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.service.EmailService;
import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.entity.*;
import com.lit_map_BackEnd.domain.work.repository.RollBackVersionRepository;
import com.lit_map_BackEnd.domain.work.repository.VersionRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lit_map_BackEnd.domain.mail.dto.MailDto;
import com.lit_map_BackEnd.domain.mail.service.MailService;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.work.entity.Confirm;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VersionServiceImpl implements VersionService{

    private final VersionRepository versionRepository;
    private final WorkRepository workRepository;
    private final CastRepository castRepository;
    private final RollBackVersionRepository rollBackVersionRepository;
    private final CastService castService;

    private final MemberRepository memberRepository;
  //  private final MailService mailService;
    private final EmailService emailService;

    // 기존의 버전 정보 업데이트하기
    @Override
    public Version changeVersion(Double versionNum, String versionName, Map<String, Object> relationship, Work work) {
        Version version = versionRepository.findByVersionNumAndWork(versionNum, work);
        version.updateVersion(versionName, relationship);

        return version;
    }

    @Override
    public VersionResponseDto findVersionByWorkAndNumber(Long workId, Double versionNum) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));
        Version version = versionRepository.findByVersionNumAndWork(versionNum, work);

        if (version == null) {
            throw new BusinessExceptionHandler(ErrorCode.VERSION_NOT_FOUND);
        }

        // 해당 버전에 관련된 캐릭터 불러오기
        List<CastResponseDto> casts = castRepository.findByVersion(version).stream()
                .map(cast -> CastResponseDto.builder()
                        .castId(cast.getId())
                        .name(cast.getName())
                        .imageUrl(cast.getImageUrl())
                        .type(cast.getType())
                        .role(cast.getRole())
                        .gender(cast.getGender())
                        .age(cast.getAge())
                        .mbti(cast.getMbti())
                        .contents(cast.getContents())
                        .build())
                .collect(Collectors.toList());


        return VersionResponseDto.builder()
                .versionNum(version.getVersionNum())
                .versionName(version.getVersionName())
                .casts(casts)
                .relationship(version.getRelationship())
                .build();
    }

    @Override
    @Transactional
    public void deleteVersion(Member member, Long workId, Double versionNum) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        MemberRoleStatus writer = work.getMember().getMemberRoleStatus();
        MemberRoleStatus memberRoleStatus = member.getMemberRoleStatus();
        if (memberRoleStatus.equals(writer) || member.getMemberRoleStatus() == MemberRoleStatus.ADMIN) {
            versionRepository.deleteByWorkAndVersionNum(work, versionNum);
        } else {
            throw new BusinessExceptionHandler(ErrorCode.WRITER_WRONG);
        }

    }

    @Override
    public List<VersionListDto> versionList(Work work) {
        // complete 된것만 가져오기
        List<Version> versions = versionRepository.findByWorkComplete(work);
        List<RollBackVersion> rollBackVersions = rollBackVersionRepository.findByWork(work);

        List<VersionListDto> list = new ArrayList<>();
        for (Version version : versions) {
            VersionListDto build = VersionListDto.builder()
                    .versionNum(version.getVersionNum())
                    .versionName(version.getVersionName())
                    .build();

            list.add(build);
        }

        for (RollBackVersion rollBackVersion : rollBackVersions) {
            VersionListDto build = VersionListDto.builder()
                    .versionNum(rollBackVersion.getVersionNum())
                    .versionName(rollBackVersion.getVersionName())
                    .build();

            list.add(build);
        }

        // versionListDto 에 Comparable을 구현하여 순서 정리
        Collections.sort(list);

        for (VersionListDto versionListDto : list) {
            System.out.println("name = " + versionListDto.getVersionName());
            System.out.println("num = " + versionListDto.getVersionNum());
        }
        return list;
    }

    @Override
    @Transactional
    public void rollBackDataSave(Long memberId, Long workId, Double versionNum) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WRITER_WRONG));
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));
        Version version = versionRepository.findByVersionNumAndWork(versionNum, work);

        if (version.getConfirm() == Confirm.COMPLETE) {
            // 수정을 누를때마다 기존의 롤백 데이터는 삭제
            if (rollBackVersionRepository.existsByWorkAndVersionNum(work, versionNum)) {
                rollBackVersionRepository.deleteRollBackVersionByWorkAndVersionNum(work, versionNum);
            }

            // 롤백 데이터 버전 저장
            RollBackVersion rollBackVersion = RollBackVersion.builder()
                    .work(version.getWork())
                    .versionName(version.getVersionName())
                    .versionNum(version.getVersionNum())
                    .confirm(version.getConfirm())
                    .originVersionId(version.getId())
                    .relationship(version.getRelationship())
                    .build();

            // 해당 버전의 연결된 캐릭터 받아오기
            List<Cast> casts = version.getCasts();

            // 받아온 캐릭터 RollBackCast에 저장
            for (Cast cast : casts) {
                // 각 캐릭터를 저장하고 rollbackversion에 연결시킨다.
                RollBackCast rollBackCast = castService.insertRollBackCast(cast);
                rollBackCast.changeRollBackVersion(rollBackVersion);
                rollBackVersion.getCasts().add(rollBackCast);
            }

            // work 에 롤백 데이터가 들어갈때 같이 들어가야함 -> 삭제 될때 같이 삭제 되어야 한다.
            work.getRollBackVersions().add(rollBackVersion);
            // 기존 데이터는 다시 승인 전으로 돌리기
            version.confirmSetting(Confirm.LOAD);

            rollBackVersionRepository.save(rollBackVersion);
        }
    }

    //추가
    @Override
    public void confirmVersion(Long versionId, Authentication authentication) {
        Version version = versionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.VERSION_NOT_FOUND));

        // 인증 객체에서 현재 사용자의 권한을 가져와 ADMIN 역할인지 확인
        version.confirmSetting(Confirm.COMPLETE);

        // 승인 완료 메일 발송
        sendApprovalEmail(versionId); //삭제 , 등록 , 수정

        versionRepository.save(version);
    }

    // admin 체크
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));
    }

    // 이메일 발송
    public void sendApprovalEmail(Long versionId) {
        // VersionId로 Version 엔티티를 조회하여 workId를 가져옴
        Version version = versionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.VERSION_NOT_FOUND));

        Work work = workRepository.findById(version.getWork().getId())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        Member member = memberRepository.findById(work.getMember().getId())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));


        // 작품 등록 승인
        String subject = "[litmap] 작품 승인 완료";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,iVBORw0qMPdgAAAAASUVORK5CYII=\\\"/>"
                + "<br><h2>작품 승인 완료</h2><h4>작품 ("
                + work.getTitle()
                + "이 등록 승인 완료되었습니다.</h4><br></div>"; //수정 승인...?

        emailService.sendEmail(member.getLitmapEmail(), subject, content);
        //삭제

        //수정 승인

    }
}
