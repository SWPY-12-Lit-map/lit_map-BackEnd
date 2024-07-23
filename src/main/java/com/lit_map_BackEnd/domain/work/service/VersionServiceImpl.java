package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.character.dto.CastResponseDto;
import com.lit_map_BackEnd.domain.character.repository.CastRepository;
import com.lit_map_BackEnd.domain.mail.dto.MailDto;
import com.lit_map_BackEnd.domain.mail.service.MailService;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Confirm;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.repository.VersionRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VersionServiceImpl implements VersionService{

    private final VersionRepository versionRepository;
    private final WorkRepository workRepository;
    private final CastRepository castRepository;

    private final MemberRepository memberRepository;
    private final MailService mailService;

    @Override
    @Transactional
    public int insertRelationship(Map<String, Object> versionRequestDto) {
        long findWorkId = ((Integer) versionRequestDto.get("workId")).longValue();
        Double version = (Double) versionRequestDto.get("version");

        Work work = workRepository.findById(findWorkId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        Version findVersion = versionRepository.findByVersionNumAndWork(version, work);

        versionRequestDto.remove("workId");
        versionRequestDto.remove("version");

        findVersion.changeRelationship(versionRequestDto);

        return 1;
    }

    // 한 작품의 여러 버전을 반환
    @Override
    public List<VersionResponseDto> findVersionByWork(Work work) {
        return versionRepository.findByWork(work).stream()
                .map(version -> VersionResponseDto.builder()
                        .versionNum(version.getVersionNum())
                        .versionName(version.getVersionName())
                        .updateTime(version.getUpdatedDate())
                        .relationship(version.getRelationship())
                        .build())
                .collect(Collectors.toList());
    }

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
    public void deleteVersion(Long workId, Double versionNum) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        versionRepository.deleteByWorkAndVersionNum(work, versionNum);
    }

    //추가
    public Version confirmVersion(Long versionId, Confirm confirm, Authentication authentication) {
        Version version = versionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.VERSION_NOT_FOUND));

        // 인증 객체에서 현재 사용자의 권한을 가져와 ADMIN 역할인지 확인
        if (confirm == Confirm.COMPLETE ) { // && isAdmin(authentication)
            version.confirmSetting(confirm);

            // 승인 완료 메일 발송
            sendApprovalEmail(versionId); //삭제 , 등록 , 수정

            return versionRepository.save(version);
        } else {
            throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));
    }




    public void sendApprovalEmail(Long versionId) {
        // VersionId로 Version 엔티티를 조회하여 workId를 가져옴
        Version version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));
        Long workId = version.getWork(); // getWorkId

        // workId를 사용하여 Work 엔티티를 조회하여 memberId를 가져옴
        Work work = workRepository.findByMemberId(workId);

        if (work == null) {
            throw new RuntimeException("Work not found for workId: " + workId);
        }

        Long memberId = work.getMember();

        // memberId를 사용하여 Member 엔티티를 조회하여 member Email을 가져옴
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found for memberId: " + memberId));

        String mailAddress = member.getEmail();
        //등록 승인
        String subject = "[litmap] 작품 승인 완료 알림";
        String text = "작품이 성공적으로 승인되었습니다.";

        //삭제

        //수정 승인

        MailDto mailDTO = new MailDto(mailAddress, subject, text);

        try {
            mailService.sendMail(mailDTO);
        } catch (MailException e) {
            // 메일 발송 실패 시 처리 로직
            e.printStackTrace(); // 예외 처리를 좀 더 구체적으로 해야 함
        }
    }
}
