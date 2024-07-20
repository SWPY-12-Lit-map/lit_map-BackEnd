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

    @GetMapping("/search")
    public List<Youtube> search(@RequestParam String query) throws Exception {
        return youtubeService.getYoutubeInfo(query);
    }
}
