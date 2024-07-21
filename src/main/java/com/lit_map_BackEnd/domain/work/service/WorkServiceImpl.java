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
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.work.dto.VersionListDto;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.*;
import com.lit_map_BackEnd.domain.work.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    /**
     *  데이터를 삽입하는 것과 업데이트하는것이 동시에 되어야 하기 때문에
     *  계속된 더티체킹을 하게 된다. 굉장히 비효율적인거 같은데 어떻게 하면 좋을까
     */

    @Override
    @Transactional
    public int saveWork(@Valid WorkRequestDto workRequestDto) {
        // 멤버 확인 ( 현재는 null 로 생성 )

        // 출판사도 확인 ( 현재는 null 로 생성 )

        Work work = null;
        Version version = null;
        // 기존에 존재하는 작품인지 아닌지 확인
        boolean isNew = false;

        // 이미 존재하는 제목이 있다면 추가 불가
        if (workRepository.existsByTitle(workRequestDto.getTitle())) {
            // 수정을 하는 것인지 확인하기 위해 기존에 작성하던 사람인지 확인
            // 중복으로 작품이 작성되는 것은 막아야 하기 떄문에 기존에 작성하던것을 불러와서 더티 체킹으로 저장
            work = workRepository.findByTitle(workRequestDto.getTitle());
            if (!work.getMember().getId().equals(workRequestDto.getMemberId())) {
                throw new BusinessExceptionHandler(ErrorCode.WRITER_WRONG);
            }
        } else {
            work = Work.builder()
                    //.title(workRequestDto.getTitle())
                    .title(workRequestDto.getTitle())
                    .content(workRequestDto.getContents())
                    .member(null)
                    .publisherName(workRequestDto.getPublisherName())
                    .publisherDate(workRequestDto.getPublisherDate())
                    .category(null)
                    .imageUrl(workRequestDto.getImageUrl())
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

            // 카테고리가 존재해야 장르를 저장한다.
        }

        // 이미지 추가
        if (workRequestDto.getImageUrl() != null && !workRequestDto.getImageUrl().isBlank()) {
            work.changeImageUrl(workRequestDto.getImageUrl());
        }

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
        if (workRequestDto.getGenre() != null && !workRequestDto.getGenre().isBlank()) {
            String[] genres = workRequestDto.getGenre().split(",");
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
        if (workRequestDto.getAuthor() != null && !workRequestDto.getAuthor().isBlank()) {
            String[] authors = workRequestDto.getAuthor().split(",");
            // 해당 작품에 관련된 작가 삭제
            workAuthorRepository.deleteByWork(work);
            for (String str : authors) {
                // 해당 작가 저장
                Author author = authorService.checkAuthor(str);
                // 작가 저장
                WorkAuthor workAuthor = WorkAuthor.builder().work(work).author(author).build();
                work.getWorkAuthors().add(workAuthor);
            }
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
        // 이미지, 작성자, 출판사, 제목, 설명 겸 작품 ID 검사
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        // 작성자의 이름이 없을 수 있다 ( 탈퇴하였을 경우 )
        String memberName = "";
        Member member = work.getMember();
        if (member == null) {
            memberName = "탈퇴한 회원입니다.";
        }

        // view 카운트 올리기
        // 트랜잭션 처리, 비관적 락 사용
        workRepository.countUpView(workId);

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

        // 기존의 작품의 버전 관련된 내용을 전부 가져오는 과정에서 그냥 0.1버전 하나만 가져오는 것으로 변경
        // 이는 초기 로딩 속도와 네트워크 효율성을 고려하여 제작
        VersionResponseDto version = versionService.findVersionByWorkAndNumber(work.getId(), 0.1);

        // 기존에 있는 버전들을 모두 반환
        List<VersionListDto> maps = versionService.versionList(work);

        return WorkResponseDto.builder()
                .workId(work.getId())
                .category(category.getName())
                .genre(workGenresList)
                .author(workAuthorsList)
                .imageUrl(work.getImageUrl())
                .memberName(memberName)
                .title(work.getTitle())
                .contents(work.getContent())
                .versions(version)
                .versionList(maps)
                .build();
    }

    @Override
    @Transactional
    public void deleteWork(Long workId) {
        workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        workRepository.deleteById(workId);
    }

    // 제출(true)과 수정/임시저장(false)을 구분하는 메소드
    private Confirm checkConfirm(boolean status) {
        if (!status) return Confirm.LOAD;
        else return Confirm.CONFIRM;
    }
}
