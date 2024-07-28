package com.lit_map_BackEnd.domain.relation.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.relation.repository.RelationRepository;

import com.lit_map_BackEnd.domain.work.entity.Work;
import com.lit_map_BackEnd.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RelationServiceImpl implements RelationService {

    private final RelationRepository relationRepository;
    private final WorkRepository workRepository;

    public List<Work> findRelatedWorks(Long workId) {
        Work baseWork = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        List<Work> relatedWorksByAuthor = relationRepository.findOtherWorksWithSameCategoryGenreSortedByAuthor(workId);

        List<Work> relatedWorksByGenre = relationRepository.findWorksWithSameCategoryAndAuthorButDifferentGenre(workId);

        Set<Work> uniqueWorks = new HashSet<>(relatedWorksByAuthor);
        relatedWorksByAuthor.clear();
        relatedWorksByAuthor.addAll(uniqueWorks);

        relatedWorksByAuthor.addAll(relatedWorksByGenre);
        System.out.println(relatedWorksByAuthor);
        return relatedWorksByAuthor;
    }


    /*
    @Autowired
    private ModelMapper modelMapper; // ModelMapper를 사용하여 DTO 변환

    public List<WorkDTO> findRelatedWorks(Long workId) {
        Work baseWork = workRepository.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));

        Category category = baseWork.getCategory();
        List<Author> authors = baseWork.getAuthors();
        List<Genre> genres = baseWork.getGenres();

        // 1순위: 같은 카테고리, 같은 작가, 같은 장르
        List<Work> relatedWorks1 = workRepository.findByCategoryAndAuthorsAndGenres(category, authors.get(0), genres.get(0));

        // 2순위: 같은 카테고리, 같은 작가, 다른 장르
        List<Work> relatedWorks2 = workRepository.findByCategoryAndAuthorsNotAndGenres(category, authors.get(0), genres.get(0));

        // 3순위: 같은 카테고리, 다른 작가, 같은 장르
        List<Work> relatedWorks3 = workRepository.findByCategoryAndGenresAndAuthorsNot(category, genres.get(0), authors.get(0));

        List<Work> allRelatedWorks = new ArrayList<>();
        allRelatedWorks.addAll(relatedWorks1);
        allRelatedWorks.addAll(relatedWorks2);
        allRelatedWorks.addAll(relatedWorks3);

        return convertToDTOs(allRelatedWorks);
    }

    private List<WorkDTO> convertToDTOs(List<Work> works) {
        return works.stream()
                .map(work -> modelMapper.map(work, WorkDTO.class))
                .collect(Collectors.toList());
    }

    public List<Work> recommendRelatedWorks(Work searchedWork) {
        List<WorkCategoryGenre> categoriesGenres = searchedWork.getWorkCategoryGenres();
        List<WorkAuthor> authors = searchedWork.getWorkAuthors();

        Set<Work> recommendedWorks = new HashSet<>(); // Set을 사용하여 중복 제거

        // 동일한 카테고리와 장르를 가진 작품 추천
        if (!categoriesGenres.isEmpty()) {
            List<Work> worksSameCategoryGenre = relationRepository.findByWorkCategoryGenresIn(categoriesGenres);
            recommendedWorks.addAll(worksSameCategoryGenre);
        }

        // 같은 작가일 때, 동일한 카테고리와 장르를 가진 작품 추천
        if (!authors.isEmpty()) {
            List<Work> worksSameAuthorCategoryGenre = relationRepository.findByWorkAuthorsInAndWorkCategoryGenresIn(authors, categoriesGenres);
            recommendedWorks.addAll(worksSameAuthorCategoryGenre);
        }

        // 다른 작가일 때, 동일한 카테고리와 장르를 가진 작품 추천
        List<Work> worksDifferentAuthorCategoryGenre = relationRepository.findByWorkAuthorsNotInAndWorkCategoryGenresIn(authors, categoriesGenres);
        recommendedWorks.addAll(worksDifferentAuthorCategoryGenre);

        return new ArrayList<>(recommendedWorks); // Set을 다시 List로 변환
    }

    public List<Work> recommendRelatedWorksById(Long workId) {
        Work searchedWork = workRepository.findById(workId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.WORK_NOT_FOUND));

        return recommendRelatedWorks(searchedWork);
    }*//*
public List<WorkResponseDto> recommendRelatedWorks(Work searchedWork) {
    List<WorkCategoryGenre> categoriesGenres = searchedWork.getWorkCategoryGenres();
    List<WorkAuthor> authors = searchedWork.getWorkAuthors();

    Set<Work> recommendedWorks = new HashSet<>();

    if (!categoriesGenres.isEmpty()) {
        List<Work> worksSameCategoryGenre = relationRepository.findByWorkCategoryGenresIn(categoriesGenres);
        recommendedWorks.addAll(worksSameCategoryGenre);
    }

    if (!authors.isEmpty()) {
        List<Work> worksSameAuthorCategoryGenre = relationRepository.findByWorkAuthorsInAndWorkCategoryGenresIn(authors, categoriesGenres);
        recommendedWorks.addAll(worksSameAuthorCategoryGenre);
    }

    List<Work> worksDifferentAuthorCategoryGenre = relationRepository.findByWorkAuthorsNotInAndWorkCategoryGenresIn(authors, categoriesGenres);
    recommendedWorks.addAll(worksDifferentAuthorCategoryGenre);

    return recommendedWorks.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
}

    private WorkResponseDto convertToDto(Work work) {
        return WorkResponseDto.builder()
                .workId(work.getId())
                .category(category.getName())
                .genre(workGenresList)
                .author(workAuthorsList)
                .imageUrl(work.getImageUrl())
                .publisherDate(work.getPublisherDate())
                .title(work.getTitle())
                .contents(work.getContent())
                .build();


    }

    public List<WorkResponseDto> recommendRelatedWorksById(Long workId) {
        Work searchedWork = workRepository.findById(workId)
                .orElseThrow(() -> new RuntimeException("Work not found"));

        return recommendRelatedWorks(searchedWork);
    }
*/

}
