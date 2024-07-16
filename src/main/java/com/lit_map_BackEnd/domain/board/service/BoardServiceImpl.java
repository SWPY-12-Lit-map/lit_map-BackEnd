package com.lit_map_BackEnd.domain.board.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.board.dto.ConfirmListDto;
import com.lit_map_BackEnd.domain.board.dto.VersionInfo;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.category.repository.CategoryRepository;
import com.lit_map_BackEnd.domain.genre.entity.Genre;
import com.lit_map_BackEnd.domain.genre.repository.GenreRepository;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.*;
import com.lit_map_BackEnd.domain.work.repository.VersionRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
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
    private final CategoryRepository categoryRepository;
    private final GenreRepository genreRepository;
    private final MemberRepository memberRepository;
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
    @Transactional(readOnly = true)
    public Map<String, List<VersionInfo>> getMyWorkList() {
        // 멤버 아이디 가져오고 그걸로 멤버 찾아서 null 확인
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        QWork work = QWork.work;
        QVersion version = QVersion.version;

        // memberId를 이용한 작성 작품 가져오기
        JPQLQuery<Long> subQuery = JPAExpressions
                .select(work.id)
                .from(work)
                .where(work.member.id.eq(member.getId()));

        List<Tuple> fetch = jpaQueryFactory
                .select(work.title, version.versionName, version.confirm)
                .from(work)
                .join(version)
                .on(version.work.id.eq(work.id))
                .where(work.id.in(subQuery))
                .fetch();

        Map<String, List<VersionInfo>> workToVersionsMap = new HashMap<>();
        for (Tuple tuple : fetch) {
            String workTitle = tuple.get(work.title);
            String versionName = tuple.get(version.versionName);
            Confirm confirm = tuple.get(version.confirm);

            VersionInfo build = VersionInfo.builder()
                    .versionName(versionName)
                    .confirm(confirm.name())
                    .build();

            workToVersionsMap.computeIfAbsent(workTitle, k -> new ArrayList<>())
                    .add(build);
        }

        return workToVersionsMap;
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<WorkResponseDto> getWorkListByView(int pageNum) {
        Slice<Work> all = workRepository.findWorks(PageRequest.of(pageNum, 1));

        return all.map(work -> WorkResponseDto.builder()
                .workId(work.getId())
                .imageUrl(work.getImageUrl())
                .title(work.getTitle())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<WorkResponseDto> getWorkListByUpdateDate(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 1);
        // 각 작품마다 가장 최신 업데이트 된 순으로 정렬
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
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWorkByCategoryAndGenre(Long categoryId, Long genreId) {
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.CATEGORY_NOT_FOUND));

        Genre findGenre = genreRepository.findById(genreId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.GENRE_NOT_FOUND));

        QWorkCategoryGenre workCategoryGenre = QWorkCategoryGenre.workCategoryGenre;
        QWork work = QWork.work;

        JPQLQuery<Long> subQuery = JPAExpressions
                .select(workCategoryGenre.work.id)
                .from(workCategoryGenre)
                .where(workCategoryGenre.category.eq(findCategory)
                        .and(workCategoryGenre.genre.eq(findGenre)));

        List<Tuple> fetch = jpaQueryFactory.select(work.id, work.title)
                .from(work)
                .where(work.id.in(subQuery))
                .fetch();

        return fetch.stream().map(tuple -> {
            Map<String, Object> map = new HashMap<>();
            map.put("workId", tuple.get(work.id));
            map.put("workTitle", tuple.get(work.title));
            return map;
        }).toList();
    }
}
