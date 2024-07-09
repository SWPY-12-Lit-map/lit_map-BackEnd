package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.character.dto.CastResponseDto;
import com.lit_map_BackEnd.domain.character.repository.CastRepository;
import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.repository.VersionRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VersionServiceImpl implements VersionService{

    private final VersionRepository versionRepository;
    private final WorkRepository workRepository;
    private final CastRepository castRepository;

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

    @Override
    public List<VersionListDto> versionList(Work work) {
        List<Version> versions = versionRepository.findByWork(work);

        List<VersionListDto> collect = versions.stream().map(version -> VersionListDto.builder()
                        .versionNum(version.getVersionNum())
                        .versionName(version.getVersionName())
                        .build())
                        .toList();

        for (VersionListDto versionListDto : collect) {
            System.out.println("name = " + versionListDto.getVersionName());
            System.out.println("num = " + versionListDto.getVersionNum());
        }
        return collect;
    }


}
