package com.lit_map_BackEnd.domain.character.service;

import com.lit_map_BackEnd.domain.character.dto.CastRequestDto;
import com.lit_map_BackEnd.domain.character.dto.CastResponseDto;
import com.lit_map_BackEnd.domain.character.entity.Cast;
import com.lit_map_BackEnd.domain.character.repository.CastRepository;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CastServiceImpl implements CastService {

    private final CastRepository castRepository;
    private final WorkRepository workRepository;

    @Override
    @Transactional
    public Cast insertCharacter(CastRequestDto castRequestDto) {
        String imageUrl = castRequestDto.getImageUrl();

        // 임시 사진 설정 하는 곳
        if (imageUrl.isBlank()) {
            imageUrl = "임시 사진";
        }

        Cast cast = null;

        // 이름이 같다면 해당 인물은 더티 체킹으로 데이터 저장
        // version와 name을 비교해서 이미 있다면 걔는 정보만 수정
        cast = castRepository.findByVersionAndName(castRequestDto.getVersion(), castRequestDto.getName());
        if (cast != null) {
            cast.changeState(castRequestDto, imageUrl);
        } else {
            cast = Cast.builder()
                    .name(castRequestDto.getName())
                    .imageUrl(imageUrl)
                    .type(castRequestDto.getType())
                    .work(castRequestDto.getWork())
                    .version(castRequestDto.getVersion())
                    .role(castRequestDto.getRole())
                    .gender(castRequestDto.getGender())
                    .age(castRequestDto.getAge())
                    .mbti(castRequestDto.getMbti())
                    .contents(castRequestDto.getContents())
                    .build();

            castRepository.save(cast);
        }

        return cast;
    }

    @Override
    public List<CastResponseDto> findCharacterByWork(Work work) {
        return castRepository.findByWork(work).stream()
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
    }
}
