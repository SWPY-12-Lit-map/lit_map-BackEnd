package com.lit_map_BackEnd.domain.board.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.board.dto.ConfirmListDto;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.entity.WorkAuthor;
import com.lit_map_BackEnd.domain.work.entity.WorkGenre;
import com.lit_map_BackEnd.domain.work.repository.VersionRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final WorkRepository workRepository;
    private final VersionRepository versionRepository;
    private final MemberRepository memberRepository;

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

    @Override
    public List<WorkResponseDto> getMyWorkList() {
        // 멤버 아이디 가져오고 그걸로 멤버 찾아서 null 확인
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // 해당 멤버 아이디로 work값 가져오기
        List<Work> byMember = workRepository.findByMember(member);

        List<WorkResponseDto> list = new ArrayList<>();
        for (Work work : byMember) {
            Category category = work.getCategory();

            List<WorkGenre> workGenres = work.getWorkGenres();
            List<String> workGenresList = new ArrayList<>();
            for (WorkGenre workGenre : workGenres) {
                String name = workGenre.getGenre().getName();
                workGenresList.add(name);
            }

            // 작가
            List<WorkAuthor> workAuthors = work.getWorkAuthors();
            List<String> workAuthorsList = new ArrayList<>();
            for (WorkAuthor workAuthor : workAuthors) {
                String name = workAuthor.getAuthor().getName();
                workAuthorsList.add(name);
            }

            List<Version> versions = work.getVersions();
            List<VersionListDto> personalVersions = new ArrayList<>();
            for (Version version : versions) {
                VersionListDto build = VersionListDto.builder()
                        .versionNum(version.getVersionNum())
                        .versionName(version.getVersionName())
                        .confirm(version.getConfirm())
                        .build();

                personalVersions.add(build);
            }

            WorkResponseDto workResponseDto = WorkResponseDto.builder()
                    .workId(work.getId())
                    .category(category.getName())
                    .genre(workGenresList)
                    .author(workAuthorsList)
                    .imageUrl(work.getImageUrl())
                    .memberName(work.getMember().getName())
                    .publisherName(work.getPublisherName())
                    .title(work.getTitle())
                    .contents(work.getContent())
                    .versionList(personalVersions)
                    .build();

            list.add(workResponseDto);
        }
        return list;
    }
}
