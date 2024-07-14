package com.lit_map_BackEnd.domain.board.service;

import com.lit_map_BackEnd.domain.board.dto.ConfirmListDto;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.repository.VersionRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import com.lit_map_BackEnd.domain.work.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final WorkRepository workRepository;
    private final VersionRepository versionRepository;

    @Override
    public List<ConfirmListDto> getConfirmData() {
        // 각 작품을 모두 가져오고 그 작품에 해당하는 버전들을 모두 가져온다.
        List<Work> all = workRepository.findAll();

        List<ConfirmListDto> list = new ArrayList<>();
        for (Work work : all) {
            // 해당 작품에 해당하는 버전들 중 confirm 상태인 것을 가져온다
            List<String> collect = versionRepository.findByWorkConfirm(work).stream()
                    .map(Version::getVersionName).toList();

            ConfirmListDto build = ConfirmListDto.builder()
                    .workTitle(work.getTitle())
                    .versionList(collect)
                    .build();

            list.add(build);
        }

        return list;
    }
}
