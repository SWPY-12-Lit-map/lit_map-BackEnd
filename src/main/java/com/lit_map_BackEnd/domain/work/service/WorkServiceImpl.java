package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.author.entity.Author;
import com.lit_map_BackEnd.domain.author.service.AuthorService;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.category.service.CategoryService;
import com.lit_map_BackEnd.domain.character.dto.CastRequestDto;
import com.lit_map_BackEnd.domain.character.entity.Cast;
import com.lit_map_BackEnd.domain.character.service.CastService;
import com.lit_map_BackEnd.domain.genre.entity.Genre;
import com.lit_map_BackEnd.domain.genre.service.GenreService;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.relation.service.RelationService;
import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.*;
import com.lit_map_BackEnd.domain.work.repository.*;
import com.lit_map_BackEnd.domain.youtube.entity.Youtube;
import com.lit_map_BackEnd.domain.youtube.service.YoutubeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Validated
public class WorkServiceImpl implements WorkService{

    private final WorkRepository workRepository;
    private final WorkAuthorRepository workAuthorRepository;
    private final WorkGenreRepository workGenreRepository;
    private final VersionRepository versionRepository;
    private final WorkCategoryGenreRepository workCategoryGenreRepository;
    private final CategoryService categoryService;
    private final GenreService genreService;
    private final AuthorService authorService;
    private final CastService castService;
    private final VersionService versionService;
    private final MemberRepository memberRepository;
    private final RelationService relationService;

    @Autowired
    private final YoutubeService youtubeService;


    /**
     *  데이터를 삽입하는 것과 업데이트하는것이 동시에 되어야 하기 때문에
     *  계속된 더티체킹을 하게 된다. 굉장히 비효율적인거 같은데 어떻게 하면 좋을까
     */

    @Override
    @Transactional
    public int saveWork(Member member, @Valid WorkRequestDto workRequestDto) {
        // 기존에 존재하는 작품인지 아닌지 확인
        boolean isNew = false;

        Work work = null;
        Version version = null;

        // 회원 증명
        MemberRoleStatus memberRoleStatus = member.getMemberRoleStatus();
        Set<MemberRoleStatus> invalidStatuses = Set.of(
                MemberRoleStatus.PENDING_MEMBER,
                MemberRoleStatus.UNKNOWN_MEMBER,
                MemberRoleStatus.WITHDRAWN_MEMBER
        );

        if (invalidStatuses.contains(memberRoleStatus)) {
            throw new BusinessExceptionHandler(ErrorCode.WRITER_INVALID);
        }

        // 이미 존재하는 작품이 있다면 기존의 작품 불러오기
        if(workRequestDto.getWorkId() != null) {
            // 수정을 하는 것인지 확인하기 위해 기존에 작성하던 사람인지 확인
            // 중복으로 작품이 작성되는 것은 막아야 하기 떄문에 기존에 작성하던것을 불러와서 더티 체킹으로 저장
            work = workRepository.findById(workRequestDto.getWorkId())
                    .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));
        } else {
            // 없다면 새롭게 추가
            work = Work.builder()
                    .title(workRequestDto.getTitle())
                    .content(workRequestDto.getContents())
                    .member(member)
                    .publisher(member.getPublisher())
                    .publisherDate(workRequestDto.getPublisherDate())
                    .view(0)
                    .build();
            isNew = true;
        }

        // 기존의 작품이 존재하지 않는다면 work 객체를 save 하여 외래키 지정이 가능하도록 수정
        if (isNew) workRepository.save(work);

        // 카테고리 추가
        Category category = null;
        if (workRequestDto.getCategory() != null && !workRequestDto.getCategory().isBlank()) {
            category = categoryService.checkCategory(workRequestDto.getCategory());
            work.changeCategory(category);
        }

        // 이미지 추가
        if (workRequestDto.getImageUrl() != null && !workRequestDto.getImageUrl().isBlank()) {
            work.changeImageUrl(workRequestDto.getImageUrl());
        } else work.changeImageUrl("https://image.litmap.store/empty/804db002-d8d4-43c5-9924-edef78c4efbd.png");


        // 설명 추가
        if (workRequestDto.getContents() != null && !workRequestDto.getContents().isBlank()) {
            work.changeContent(workRequestDto.getContents());
        }

        // 버전 이름을 따로 정해주지 않으면 버전넘버가 이름을 대체
        String versionName = (workRequestDto.getVersionName() != null && !workRequestDto.getVersionName().isBlank()) ?
                workRequestDto.getVersionName() : String.valueOf(workRequestDto.getVersion());


        // 버전 저장 ( 중복된 버전을 저장할 수 없다 )
        // 기존에 존재한다면 수정
        if (versionRepository.existsByVersionNumAndWork(workRequestDto.getVersion(), work)) {
            // 기존에 존재하면 수정
            version = versionService.changeVersion(workRequestDto.getVersion(), versionName
                    , workRequestDto.getRelationship(), work);

            version.confirmSetting(checkConfirm(workRequestDto.isConfirmCheck()));
        } else {
            // 없다면 추가
            version = Version.builder()
                    .work(work)
                    .versionNum(workRequestDto.getVersion())
                    .versionName(versionName)
                    .relationship(workRequestDto.getRelationship())
                    .confirm(checkConfirm(workRequestDto.isConfirmCheck()))
                    .build();

            work.getVersions().add(version);
        }

        // 장르 저장 ( 중복 저장 되지 않도록 저장 )
        if (workRequestDto.getGenre() != null && workRequestDto.getGenre().size() != 0) {
        List<String> genres = workRequestDto.getGenre();
            // 작품에 관련된 장르 전체 삭제
            workGenreRepository.deleteByWork(work);
            for (String str : genres) {
                Genre genre = genreService.checkGenre(str);
                // 장르 다시 저장
                WorkGenre workGenre = WorkGenre.builder().work(work).genre(genre).build();
                work.getWorkGenres().add(workGenre);
                // 이미 중복된 카테고리와 장르가 저장되어 있다면 다시 저장할 필요 X
                if (!workCategoryGenreRepository.existsByWorkAndCategoryAndGenre(work, category, genre)) {
                    WorkCategoryGenre build = WorkCategoryGenre.builder()
                            .work(work)
                            .category(category)
                            .genre(genre)
                            .build();

                    work.getWorkCategoryGenres().add(build);
                }
            }
        }

        // 작가 저장 ( 중복 저장 되지 않도록 저장 )
        List<String> author = workRequestDto.getAuthor();
        if (author != null && !author.isEmpty()) {
            // 해당 작품에 관련된 작가 삭제
            workAuthorRepository.deleteByWork(work);
            int size = author.size();
            for (int i = 0; i < size; i++) {
                work.mainAuthorSetting(author.get(0));
                // 작가 저장
                Author authorName = authorService.checkAuthor(author.get(i));
                WorkAuthor workAuthor = WorkAuthor.builder().work(work).author(authorName).build();
                work.getWorkAuthors().add(workAuthor);
            }
        } else {
            work.mainAuthorSetting("미상");
        }

        // 각 캐릭터 저장
        // 만약 캐릭터를 아무것도 설정하지 않아도 한개의 데이터는 넘어올수있다.
        // 그 한개의 데이터는 임시 데이터라도 넣어달라고 한다.
        // 캐릭터 또한 중복 저장되지 않도록 주의해야 한다.
        // 문제점 : 만약 작품에 중복된 캐릭터가 존재한다면 어떻게 처리할 것인가
        List<CastRequestDto> casts = workRequestDto.getCasts();
        for (CastRequestDto cast : casts) {
            cast.setWork(work);
            cast.setVersion(version);
            // 캐릭터의 사진이 없을때 저장하는 대체 이미지
            Cast findCast = castService.insertCharacter(cast);
            work.getCasts().add(findCast);
            version.getCasts().add(findCast);
        }

        if (!isNew) workRepository.save(work);

        return 1;
    }

    // 작품 상세 내용 가져오기 ( 승인이 완료된 내용만 가져오기 )
    @Transactional
    @Override
    public WorkResponseDto getWork(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        WorkResponseDto workData = getWorkData(workId, 0.1);

        List<VersionListDto> maps = versionService.versionList(work);

        workData.setVersionList(maps);

        // view count 올리기
        workRepository.countUpView(workId);

        // 명시적으로 호출을 해서 변경 사항이 있다는 것을 확인
        workRepository.save(work);

        return workData;
    }

    @Override
    @Transactional
    public void deleteWork(Member member, Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        if (member.getId().equals(work.getMember().getId()) || member.getMemberRoleStatus() == MemberRoleStatus.ADMIN) {
            workRepository.deleteById(workId);
        } else {
            throw new BusinessExceptionHandler(ErrorCode.WRITER_WRONG);
        }
    }

    // 제출(true)과 수정/임시저장(false)을 구분하는 메소드
    private Confirm checkConfirm(boolean status) {
        if (!status) return Confirm.LOAD;
        else return Confirm.CONFIRM;
    }

    // 상세 작품과 수정 시 가져오는 WorkResponse 때문에 메소드화로 변경
    public WorkResponseDto getWorkData(Long workId, Double versionNum) {
        // 이미지, 작성자, 출판사, 제목, 설명 겸 작품 ID 검사
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        // 작성자의 이름이 없을 수 있다 ( 탈퇴하였을 경우 )
        String memberName = "";
        Member member = work.getMember();
        if (member == null) memberName = "탈퇴한 회원입니다.";
        else memberName = member.getNickname();

        // 카테고리
        Category category = categoryService.checkCategory(work.getCategory().getName());
        if (category == null) {
            throw new BusinessExceptionHandler(ErrorCode.CATEGORY_NOT_FOUND);
        }

        // 장르 ( 장르 이름을 전달 )
        List<WorkGenre> workGenres = workGenreRepository.findByWork(work);
        List<String> workGenresList = new ArrayList<>();
        for (WorkGenre workGenre : workGenres) {
            String name = workGenre.getGenre().getName();
            workGenresList.add(name);
        }

        // 작가
        List<WorkAuthor> workAuthors = workAuthorRepository.findByWork(work);
        List<String> workAuthorsList = new ArrayList<>();
        for (WorkAuthor workAuthor : workAuthors) {
            String name = workAuthor.getAuthor().getName();
            workAuthorsList.add(name);
        }

        // 이는 초기 로딩 속도와 네트워크 효율성을 고려하여 제작
        VersionResponseDto version = versionService.findVersionByWorkAndNumber(work.getId(), versionNum);

        //연관 작품 가져오기
      //  List<Work> recommendedWorks = relationService.recommendRelatedWorks(work);

        // Youtube 정보 조회
//        String workTitle = work.getTitle(); // 작품의 제목 가져오기
//        List<Youtube> youtubeInfo = null;
//
//        try {
//            youtubeInfo = youtubeService.getYoutubeInfo(workTitle);
//        } catch (Exception e) {
//            throw new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND);
//        }
//        List<Youtube> youtubeList = new ArrayList<>();
//
//        if (youtubeInfo != null) {
//            for (Youtube info : youtubeInfo) {
//                Youtube youtube = new Youtube(
//                        info.getTitle(),
//                        info.getVideoUrl(),
//                        info.getThumbnailUrl(),
//                        info.getViewCount(),
//                        info.getUploadDate()
//                );
//                youtubeList.add(youtube);
//            }
//        }


        return WorkResponseDto.builder()
                .workId(work.getId())
                .category(category.getName())
                .genre(workGenresList)
                .author(workAuthorsList)
                .imageUrl(work.getImageUrl())
                .publisherDate(work.getPublisherDate())
                .memberName(memberName)
                .title(work.getTitle())
                .contents(work.getContent())
                .versions(version)
                //.youtubes(youtubeList)
                .build();
    }
}
