package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.admin.service.AdminAuthService;
import com.lit_map_BackEnd.domain.character.dto.CastResponseDto;
import com.lit_map_BackEnd.domain.character.entity.Cast;
import com.lit_map_BackEnd.domain.character.entity.RollBackCast;
import com.lit_map_BackEnd.domain.character.repository.CastRepository;
import com.lit_map_BackEnd.domain.character.service.CastService;
import com.lit_map_BackEnd.domain.mail.dto.MailWorkDto;
import com.lit_map_BackEnd.domain.mail.service.MailService;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.entity.*;
import com.lit_map_BackEnd.domain.work.repository.RollBackVersionRepository;
import com.lit_map_BackEnd.domain.work.repository.VersionRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.work.entity.Confirm;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
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
    private final MailService mailService;
    private final AdminAuthService adminAuthService;


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



    public MailWorkDto sendMailWithTemplate(Long versionId, String subject, String content) {
        Version version = versionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.VERSION_NOT_FOUND));

        Work work = workRepository.findById(version.getWork().getId())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        Member member = memberRepository.findById(work.getMember().getId())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        String litmapEmail = member.getLitmapEmail();

        mailService.sendEmail(litmapEmail, subject, content);

        return new MailWorkDto(litmapEmail, work);
    }

    public void approveMail(Long versionId) {

        if (!adminAuthService.isAdmin()) {
            throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
        }
        Version version = versionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.VERSION_NOT_FOUND));

        Work work = workRepository.findById(version.getWork().getId())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        version.confirmSetting(Confirm.COMPLETE);
        versionRepository.save(version);

        String subject = "[litmap] 작품 승인 완료";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,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\"/>"
                + "<br><h2>작품 승인 완료</h2><h4>등록하신 작품 { "
                + work.getTitle()
                + " }이 등록 승인 완료되었습니다.</h4><br><br><br></div>";

        MailWorkDto result = sendMailWithTemplate(versionId, subject, content);
    }


    public void declineMail(Long versionId, String reason) {
        if (!adminAuthService.isAdmin()) {
            throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
        }
            Version version = versionRepository.findById(versionId)
                    .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.VERSION_NOT_FOUND));

            Work work = workRepository.findById(version.getWork().getId())
                    .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

            version.confirmSetting(Confirm.LOAD); //반려
            versionRepository.save(version);

            String subject = "[litmap] 작품 승인 반려";  //blob data 수정
            String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAI4AAAAyCAYAAACK9eMGAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAcZSURBVHgB7Z19jBxlHcc/M7vblu5Ra9HaKrZ7d3tt4xulQKSmkWrUNCJRE4n9A08TXkQbUNu73tFKWLRy7d0VgQRpSYhRE1FqpMTEgGJtQk3QVEEEgr1tOaGGopUUOa7c3e48fJ+7Fu6uu9d7mX2bmU9ynXnmefbZ2Xm+8/s973WIKEgG3HNYviiHtzAG8wzGtdfn8erBDfy3j5AT7yT9Ij5jYHUb2aPUEBnWxmdzdE0CPq3gWv2GCyCfnDUu3eskFzNOOF2kf6n0H8N/jnt4N7i491MidN8/O8iFN+9hT34qn4s7cD4+Y6gdMqTm1+HeaHjxenAWT+/ezTwHx/fnKObYf0pRRsrz5RzOxnZ6fg5ZpkqckJKRK0rStMnBbJFY5hMqzKMD5Jq38q+XmCahFM52GpbEcHfrAa6rJevoB7I0t7VweCszJHTC6ab+4wZ3j04XEiIkmON5zLVtHN6LD7iEiJ2kr/KI/Z6QiUayeUwueZVforGExuKo9fgZD37ihOtleU0/9qZN9NyNz4RCOB2kUjqo9RAe0egFeQZyX9pE73OUgMA/yIx+Y4L4H/QgzyMkqLPyTpc3LmkpkWgsgbc4dTRuUMupgVBgTsrWbNnM4TsoMYEWjnVRBucuQoAs6uMuzjUbyT5DGQi0q0oQu4XAY4x6rbf9mQvXlEs0lsBaHNvJJ2vTTLDReKDzlRZ69kMP5SSwFkcDg5cTbIv60BCsaiW7nwoQWIsj872+toZbJ4fqMq9oGHtzG9n7qCCBFI6dIiEr/lEChkRzJIf3hXaO/IMKE0hTfg69F+swm2Bxn0SzqhpEYwmkxXGYtRQ8gkCMxP9h8JpWnq+oaxpPQIXjpQgIrfzzKR2eosoIpKvyMEkiSkoghaMe1FlElJRQzceJ8I9IOBHTIhJOxLSoaeFkSM3pJLWIiLJTs8K5jRXnJYnvc0h8iIiyU5P9ONtofH+C3O90uoKIilBzwrGuycH9jQYwI9FUkJoSzk4alhliDxtMPREVpWbqOB00XuThPhaJpjqoCeF0kb4ijvNHQreQrnqpeuF003idDnYF4rlEVA1VXcfZQUObwdlOxDC3k75sCN49UZpZ5B79Dr0ndpA+X1ahw8P5Uxs9u0bl8cGBInnMIffkILPnOuSb7aSxVrIPdJJepbG/GwzeI60c/sXptFUrHLmnNmzVJuIt8ph2FeK6idIMEl/N8FIZMx+cqxy8hMK7RuXRHS+SxyDuJTCEWq0ddisUXXrAwVmgeuXXDLyqcHULR+6p09ipKBFjyOPcISE8VCjOJfZtFfZyJoksUZfS9498Fjux/2KmQFUJ5y7Sswcw94ZgWcu0aCf7SLG4Lho/LwszaeHkie3YwnP/s+edNKpvzBkjHIUb5O4yklj98GzncVSNcH7Iyvlv0Pdr3fAniCiICvgePZ9LC8WpaFN+rumwy6ZliW4pJBpLVbSqfsDSxXn6DugWI9FMgBWHDitVrDkdT4z+U0E/qeP+GO4JfMHsy5GrV75XF4qtuMWx67vtbhLh2Rhg5hjyV2ym9xgzQK2v73XR1D8S8lYXSOLdRG+v0rxQaH1aRYXTwZIGiWafbmspEZNG9ZN1qn9MtNfy42dbjKjYb76dxmGqVEw421l2QQxP405E82mmiJrTP54oXk3x9d7wxkpnkiP/jRiJgrusnuTkoTqStq70tJrkw/tUx8ifyOMq7Lw09h4qgMzfp2QJf6XTdxAxaTzMzbINd54t3Wv0H0wyt+ALad2PPaqivU79Myk17/eOc3vP6u/DpwMDeMdVEb47JjGNzqfswtENqzPJ7Hasm42YEm0cOVjoeobUojoSHerdPa7e3eH+LzXPXVmdW+WGni70GT3/r8qqrJd1OZph7cNFvpIE/25Sed0j0VrBHjh9vazC2Uljy0jHU4Sf1OGmbe+unNQr6gv77o1kBySg/zC8k93E2LlNSYr/7wnFakplE47c0zapdsYbM0eMRS/jl/Uydo+EzAKNQ/1WLdWrT7uksyFh7NUwxrHi8XZV7JlDFGURjgbK7NhHOxG+cKqH/bMq0G/JHV1md+WSAOT+XXv+yTjx5zXW91MX9/ZNHPr7xLmZ3S1ki7qqkfqoKa9wruTK2KU8If/ItUTMmAwfqKtjcKusytclmnfqUp/qKj9SK2rXxlO7WKhu80XFXa/TZg+vWQJ6QqLq2Ex2T6E8Ja5bu2naUOw75SUWFGqsl0w4O3jXuS5/u19m9HIifCHDs31qXHxETeMXJIbOfnK7MvSO6SlW3eZBHR7cyYplhlyr0n1O9Z+5Z+bmarTbO6a4JQosKfadp0SjdIzpN3K207AGn2nnyAEpf6HBfS8lpp+h3vEP7/ssr08w9D5KgP1t46910rTSGZ7G4C8ebn8bh/4y+loX70m28vLrk83jOi5K3Mtfh/CZNwEgcQOnKFrfYwAAAABJRU5ErkJggg==\"/>"
                    + "<br><h2>작품 승인 반려 </h2><h4>등록하신 작품 { "
                    + work.getTitle()
                    + " }이 등록 승인이 반려되었습니다.</h4><br><br><h4>반려 사유 : "
                    +  reason
                    + " </h4><br><br><br></div>";

            MailWorkDto result = sendMailWithTemplate(versionId, subject, content);

    }

}
