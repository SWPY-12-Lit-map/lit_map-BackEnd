package com.lit_map_BackEnd.domain.work.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.author.entity.Author;
import com.lit_map_BackEnd.domain.author.repository.AuthorRepository;
import com.lit_map_BackEnd.domain.author.service.AuthorService;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.category.repository.CategoryRepository;
import com.lit_map_BackEnd.domain.category.service.CategoryService;
import com.lit_map_BackEnd.domain.genre.entity.Genre;
import com.lit_map_BackEnd.domain.genre.repository.GenreRepository;
import com.lit_map_BackEnd.domain.genre.service.GenreService;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.work.dto.WorkRequestDto;
import com.lit_map_BackEnd.domain.work.entity.*;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService{

    private final WorkRepository workRepository;
    private final CategoryService categoryService;
    private final GenreService genreService;
    private final AuthorService authorService;

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
}
