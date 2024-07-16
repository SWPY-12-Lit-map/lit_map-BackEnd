package com.lit_map_BackEnd.domain.board.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.board.dto.ConfirmListDto;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.*;
import com.lit_map_BackEnd.domain.work.repository.VersionRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import com.lit_map_BackEnd.domain.work.service.WorkCategoryGenreService;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final WorkRepository workRepository;
    private final VersionRepository versionRepository;
    private final MemberRepository memberRepository;
    private final WorkCategoryGenreService workCategoryGenreService;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional(readOnly = true)
    public List<ConfirmListDto> getConfirmData() {
        QWork work = QWork.work;
        QVersion version = QVersion.version;

        List<Tuple> results = jpaQueryFactory
                .select(work.title, version.versionName)
                .from(work)
                .join(version).on(version.work.eq(work))
                .where(version.confirm.eq(Confirm.CONFIRM))
                .fetch();

        Map<String, List<String>> workToVersionsMap = new HashMap<>();
        for (Tuple tuple : results) {
            String workTitle = tuple.get(work.title);
            String versionName = tuple.get(version.versionName);

            workToVersionsMap.computeIfAbsent(workTitle, k -> new ArrayList<>())
                    .add(versionName);
        }

        return workToVersionsMap.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> ConfirmListDto.builder()
                        .workTitle(entry.getKey())
                        .versionList(entry.getValue())
                        .build())
                .collect(Collectors.toList());
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

    @Override
    public Slice<WorkResponseDto> getWorkListByView(int pageNum) {
        Slice<Work> all = workRepository.findWorks(PageRequest.of(pageNum, 1));

        return all.map(work -> WorkResponseDto.builder()
                .workId(work.getId())
                .imageUrl(work.getImageUrl())
                .title(work.getTitle())
                .build());
    }

    @Override
    public Slice<WorkResponseDto> getWorkListByUpdateDate(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 1);
        Page<Long> latestVersions = versionRepository.findLatestUpdateDates(pageable);

        List<Work> list = latestVersions.stream()
                .map(id -> workRepository.findById(id)
                        .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND)))
                .toList();

        Slice<Work> slice = new SliceImpl<>(list, pageable, latestVersions.hasNext());

        return slice.map(work -> WorkResponseDto.builder()
                .workId(work.getId())
                .imageUrl(work.getImageUrl())
                .title(work.getTitle())
                .build());
    }

    @Override
    public List<WorkResponseDto> getWorkByCategoryAndGenre(Long categoryId, Long genreId) {
        List<WorkCategoryGenre> works = workCategoryGenreService.findWorks(categoryId, genreId);

        return works.stream().map(workCategoryGenre -> WorkResponseDto.builder()
                .workId(workCategoryGenre.getWork().getId())
                .title(workCategoryGenre.getWork().getTitle())
                .build()
        ).toList();

    }
}
