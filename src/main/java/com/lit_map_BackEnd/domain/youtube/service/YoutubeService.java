package com.lit_map_BackEnd.domain.youtube.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.youtube.entity.Youtube;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;

@Service
public class YoutubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Youtube> getYoutubeInfo(String query) throws Exception {
        if (query == null || query.isEmpty()) {
            throw new BusinessExceptionHandler(ErrorCode.KEYWORD_NOT_FOUND);
        }
        
        //url 포맷 설정 , 기본 값 10개 (Results = 10)
        String url = String.format(
                "https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&maxResults=10&type=video&key=%s",
                query, apiKey); 

        String response = restTemplate.getForObject(url, String.class);
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode items = jsonNode.get("items");

        List<Youtube> youtubes = new ArrayList<>();

        for (JsonNode item : items) {
            String videoId = item.get("id").get("videoId").asText();
            String title = item.get("snippet").get("title").asText();
            String thumbnailUrl = item.get("snippet").get("thumbnails").get("default").get("url").asText();
            String uploadDate = item.get("snippet").get("publishedAt").asText();

            // 유투브 api로 값 가져오기
            String videoDetailsUrl = String.format(
                    "https://www.googleapis.com/youtube/v3/videos?part=statistics&id=%s&key=%s",
                    videoId, apiKey);
            String videoDetailsResponse = restTemplate.getForObject(videoDetailsUrl, String.class);
            JsonNode videoDetailsNode = objectMapper.readTree(videoDetailsResponse);
            long viewCount = videoDetailsNode.get("items").get(0).get("statistics").get("viewCount").asLong();

            // 유투브 객체 생성
            Youtube youtube = new Youtube(title, "https://www.youtube.com/watch?v=" + videoId, thumbnailUrl, viewCount, uploadDate);

            youtubes.add(youtube);
        }

        return youtubes;
    }
}
