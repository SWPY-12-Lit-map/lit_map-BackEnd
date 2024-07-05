package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.author.entity.Author;
import com.lit_map_BackEnd.domain.author.service.AuthorService;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.category.service.CategoryService;
import com.lit_map_BackEnd.domain.character.dto.CastResponseDto;
import com.lit_map_BackEnd.domain.character.service.CastService;
import com.lit_map_BackEnd.domain.genre.entity.Genre;
import com.lit_map_BackEnd.domain.genre.service.GenreService;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.work.dto.VersionResponseDto;
import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.*;
import com.lit_map_BackEnd.domain.work.repository.WorkAuthorRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkGenreRepository;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService{

    private final WorkRepository workRepository;
    private final WorkAuthorRepository workAuthorRepository;
    private final WorkGenreRepository workGenreRepository;
    private final CategoryService categoryService;
    private final GenreService genreService;
    private final AuthorService authorService;
    private final CastService castService;
    private final VersionService versionService;

    @Override
    @Transactional
    public int saveWork(WorkRequestDto workRequestDto) {
        // 멤버 확인 ( 현재는 null 로 생성 )


        if (workRepository.existsByTitle(workRequestDto.getTitle())) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_WORK_NAME);
        }

        String versionName = "";
        if (workRequestDto.getVersionName().isEmpty()) {
            versionName = String.valueOf(workRequestDto.getVersion());
        } else {
            versionName = workRequestDto.getVersionName();
        }

        Category category = categoryService.checkCategory(workRequestDto.getCategory());

        Work work = Work.builder()
                .title(workRequestDto.getTitle())
                .content(workRequestDto.getContents())
                .member(null)
                .publisherName(workRequestDto.getPublisherName())
                .category(category)
                .imageUrl(workRequestDto.getImageUrl())
                .view(0)
                .build();

        Version version = Version.builder()
                .work(work)
                .versionNum(workRequestDto.getVersion())
                .versionName(versionName)
                .confirm(Confirm.LOAD)
                .build();
        work.getVersions().add(version);

        // 장르 저장
        String[] genres = workRequestDto.getGenre().split(",");
        for (String str : genres) {
            Genre genre = genreService.checkGenre(str);
            WorkGenre workGenre = WorkGenre.builder().work(work).genre(genre).build();
            work.getWorkGenres().add(workGenre);
        }

        // 작가 저장
        String[] authors = workRequestDto.getAuthor().split(",");
        for (String str : authors) {
            Author author = authorService.checkAuthor(str);
            WorkAuthor workAuthor = WorkAuthor.builder().work(work).author(author).build();
            work.getWorkAuthors().add(workAuthor);
        }

        workRepository.save(work);
        return 1;
    }

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

        // 캐릭터
        List<CastResponseDto> characterByWork = castService.findCharacterByWork(work);

        // 버전 정보 및 내용
        List<VersionResponseDto> versionByWork = versionService.findVersionByWork(work);

        return WorkResponseDto.builder()
                .category(category.getName())
                .genre(workGenresList)
                .author(workAuthorsList)
                .imageUrl(work.getImageUrl())
                .memberName(memberName)
                .title(work.getTitle())
                .contents(work.getContent())
                .casts(characterByWork)
                .versions(versionByWork)
                .build();
    }
}
