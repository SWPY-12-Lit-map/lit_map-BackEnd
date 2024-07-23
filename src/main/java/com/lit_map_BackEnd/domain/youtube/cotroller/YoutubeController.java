package com.lit_map_BackEnd.domain.youtube.cotroller;

import com.lit_map_BackEnd.domain.youtube.entity.Youtube;
import com.lit_map_BackEnd.domain.youtube.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class YoutubeController {

    @Autowired
    private YoutubeService youtubeService;

    @GetMapping("/youtube")
    public List<Youtube> search(@RequestParam String query) throws Exception {
        return youtubeService.getYoutubeInfo(query);
    }

    public List<Work> recommendRelatedWorks(Work searchedWork) {
        List<Category> categories = new ArrayList<>(searchedWork.getCategories());

        // 작가가 있는 경우에만 같은 카테고리를 가진 작품 추천
        if (searchedWork.getAuthor() != null) {
            return workRepository.findByAuthorAndCategoriesInOrderByNamе(searchedWork.getAuthor(), categories);
        } else {
            // 작가가 null인 경우, 동일한 카테고리를 가진 모든 작품 추천
            return workRepository.findByCategoriesInOrderByNamе(categories);
        }
    }

}
