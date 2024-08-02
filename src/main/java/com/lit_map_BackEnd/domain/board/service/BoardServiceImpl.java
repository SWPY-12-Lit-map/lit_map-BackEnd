package com.lit_map_BackEnd.domain.board.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.board.dto.*;
import com.lit_map_BackEnd.domain.board.entity.MainBanner;
import com.lit_map_BackEnd.domain.board.repository.BannerRepository;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.category.repository.CategoryRepository;
import com.lit_map_BackEnd.domain.category.service.CategoryService;
import com.lit_map_BackEnd.domain.genre.entity.Genre;
import com.lit_map_BackEnd.domain.genre.repository.GenreRepository;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.*;
import com.lit_map_BackEnd.domain.work.repository.VersionRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkAuthorRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final JPAQueryFactory jpaQueryFactory;
    private final WorkRepository workRepository;
    private final VersionRepository versionRepository;
    private final CategoryRepository categoryRepository;
    private final GenreRepository genreRepository;
    private final MemberRepository memberRepository;
    private final CategoryService categoryService;
    private final WorkAuthorRepository workAuthorRepository;
    private final BannerRepository bannerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WorkResponseDto> getConfirmData() {
        QWork work = QWork.work;
        QVersion version = QVersion.version;

        List<Tuple> results = jpaQueryFactory
                .select(work.id, work.title,
                        version.versionName, version.updatedDate)
                .from(work)
                .join(version).on(version.work.eq(work))
                .where(version.confirm.eq(Confirm.CONFIRM))
                .fetch();

        Map<Long, WorkResponseDto> workToVersionsMap = new HashMap<>();
        for (Tuple tuple : results) {
            Long workId = tuple.get(work.id);
            String workTitle = tuple.get(work.title);
            String versionName = tuple.get(version.versionName);
            LocalDateTime updatedDate = tuple.get(version.updatedDate);

            WorkResponseDto workResponseDto = workToVersionsMap.computeIfAbsent(workId, id -> WorkResponseDto.builder()
                    .workId(workId)
                    .title(workTitle)
                    .versionList(new ArrayList<>())
                    .build());

            VersionListDto build = VersionListDto.builder()
                    .versionName(versionName)
                    .lastUpdateDate(updatedDate)
                    .build();
            workResponseDto.getVersionList().add(build);
        }

        return new ArrayList<>(workToVersionsMap.values());
    }

    @Override
    @Transactional(readOnly = true)
    public MyWorkListResponseDto getMyWorkList(Long memberId) {
        // 멤버 아이디 가져오고 그걸로 멤버 찾아서 null 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // 각 멤버의 작품을 가져와서 map에 미리 저장
        List<Work> byMember = workRepository.findByMember(member);
        Map<Long, MyWorkListDto> map = new HashMap<>();

        // map에 해당 작품 정보 미리 저장
        for (Work work : byMember) {
            String publisher = null;
            if (work.getPublisher() != null) {
                publisher = work.getPublisher().getPublisherName();
            }

            MyWorkListDto build = MyWorkListDto.builder()
                    .workId(work.getId())
                    .title(work.getTitle())
                    .category(work.getCategory().getName())
                    .mainAuthor(work.getMainAuthor())
                    .publisher(publisher)
                    .build();

            map.put(work.getId(), build);
        }

        // 2. 각 작품의 버전 리스트 삽입
        QWork work = QWork.work;
        QVersion version = QVersion.version;
        // memberId를 이용한 작성 작품 가져오기
        JPQLQuery<Long> subQuery = JPAExpressions
                .select(work.id)
                .from(work)
                .where(work.member.id.eq(member.getId()));

        List<Tuple> fetch = jpaQueryFactory
                .select(work.id, version.id, version.versionName,
                        version.updatedDate, version.confirm, version.versionNum)
                .from(work)
                .join(version)
                .on(version.work.id.eq(work.id))
                .where(work.id.in(subQuery))
                .fetch();

        for (Tuple tuple : fetch) {
            Long workId = tuple.get(work.id);
            Long versionId = tuple.get(version.id);
            String versionName = tuple.get(version.versionName);
            Double versionNum = tuple.get(version.versionNum);
            LocalDateTime versionUpdateDate = tuple.get(version.updatedDate);
            Confirm confirm = tuple.get(version.confirm);

            VersionListDto build = VersionListDto.builder()
                    .versionId(versionId)
                    .versionName(versionName)
                    .versionNum(versionNum)
                    .lastUpdateDate(versionUpdateDate)
                    .confirm(confirm)
                    .build();

            // 만들어진 버전 정보 반환값에 저장
            MyWorkListDto myWorkListDtos = map.get(workId);
            myWorkListDtos.getVersionLists().add(build);
        }

        // 전체 작품 갯수
        int totalSize = map.values().stream()
                .mapToInt(dto -> dto.getVersionLists().size())
                .sum();

        return MyWorkListResponseDto.builder()
                .totalCount(totalSize)
                .list(new ArrayList<>(map.values()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<WorkResponseDto> getWorkListByView(int pageNum) {
        Slice<Work> all = workRepository.findWorks(PageRequest.of(pageNum, 10));

        return all.map(work -> WorkResponseDto.builder()
                .workId(work.getId())
                .imageUrl(work.getImageUrl())
                .title(work.getTitle())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<WorkResponseDto> getWorkListByUpdateDate(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 10);

        // 각 작품마다 가장 최신 업데이트 된 순으로 정렬
        Slice<Object[]> latestUpdateDates = versionRepository.findLatestUpdateDates(pageable);

        List<WorkResponseDto> workResponseDtos = latestUpdateDates.getContent().stream()
                .map(result -> {
                    Long workId = (Long) result[0];
                    String name = "";

                    Work work = workRepository.findById(workId)
                            .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

                    // 제작자를 찾고 만약 없으면 미상으로 넘기기
                    if (work.getMember() == null) name = "미상";
                    else name = work.getMember().getName();

                    return WorkResponseDto.builder()
                            .workId(work.getId())
                            .imageUrl(work.getImageUrl())
                            .title(work.getTitle())
                            .memberName(name)
                            .build();

                }).toList();

        return new SliceImpl<>(workResponseDtos, pageable, latestUpdateDates.hasNext());
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

        List<Tuple> fetch = jpaQueryFactory
                .select(work.id, work.title)
                .from(work)
                .join(workCategoryGenre)
                .on(work.id.eq(workCategoryGenre.work.id))
                .where(workCategoryGenre.category.eq(findCategory)
                        .and(workCategoryGenre.genre.eq(findGenre)))
                .fetch();

        return fetch.stream().map(tuple -> {
            Map<String, Object> map = new HashMap<>();
            map.put("workId", tuple.get(work.id));
            map.put("workTitle", tuple.get(work.title));
            return map;
        }).toList();
    }

    @Override
    public Map<String, CategoryResultDto> findWorksBySearch(SearchDto searchDto) {
        Map<String, CategoryResultDto> result = new HashMap<>();

        List<Category> categories = categoryService.getCategories();
        for (Category category : categories) {
            result.put(category.getName(), new CategoryResultDto(0, new ArrayList<>()));
        }

        List<Work> worksByQuestion = new ArrayList<>();
        String question = searchDto.getQuestion();
        switch (searchDto.getSearchType()) {
            case TITLE:
                // 제목으로 검색하는 로직
                worksByQuestion = workRepository.findWorksByTitle(question);
                break;
            case CONTENTS:
                // 내용으로 검색하는 로직
                worksByQuestion = workRepository.findWorksByContents(question);
                break;
            case AUTHOR:
                // 저자로 검색하는 로직
                worksByQuestion = workAuthorRepository.findByAuthorName(question);
                break;
            case PUBLISHER:
                // 출판사로 검색하는 로직
                worksByQuestion = workRepository.findWorksByPublisherName(question);
                break;
            case MEMBER:
                // 회원으로 검색하는 로직
                worksByQuestion = workRepository.findWorksByMemberNickName(question);
                break;
            case TITLE_AND_CONTENTS:
                // 제목과 내용 모두 검색하는 로직
                worksByQuestion = workRepository.findWorksByTitleAndContents(question);
                break;
        }

        // 각 검색 이후 return 값 설정
        return processWorks(worksByQuestion, result);
    }

    @Override
    public Map<String, Long> getWorksCount(Long memberId) {
        Map<String, Long> map = new HashMap<>();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // 각 멤버의 작품을 검색하고 각 작품의 버전을 검색해서 complete와 나머지를 따로 검색
        QWork work = QWork.work;
        QVersion version = QVersion.version;
        long fullCount = jpaQueryFactory
                .select(Expressions.constant(member.getId()))
                .from(version)
                .join(work)
                .on(version.work.id.eq(work.id))
                .where(work.member.id.eq(member.getId()))
                .fetchCount();

        long completeCount = jpaQueryFactory
                .select(Expressions.constant(member.getId()))
                .from(version)
                .join(work)
                .on(version.work.id.eq(work.id))
                .where(work.member.id.eq(member.getId())
                        .and(version.confirm.eq(Confirm.COMPLETE)))
                .fetchCount();

        map.put("작성한 글", completeCount);
        map.put("작성중인 글", fullCount - completeCount);

        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getBannerImages() {
        return bannerRepository.findAll().stream().map(MainBanner::getImageUrl).toList();
    }

    private Map<String, CategoryResultDto> processWorks(List<Work> worksByQuestion, Map<String, CategoryResultDto> map) {
        for (Work work : worksByQuestion) {
            String name = work.getCategory().getName();
            CategoryResultDto categoryResult = map.getOrDefault(name, new CategoryResultDto(0, new ArrayList<>()));

            categoryResult.countUp();
            categoryResult.getWorks().add(convertToWorkResponseDto(work));

            map.put(name, categoryResult);
        }
        return map;
    }

    private WorkResponseDto convertToWorkResponseDto(Work work) {
        return WorkResponseDto.builder()
                .workId(work.getId())
                .mainAuthor(work.getMainAuthor())
                .imageUrl(work.getImageUrl())
                .title(work.getTitle())
                .build();
    }
}
