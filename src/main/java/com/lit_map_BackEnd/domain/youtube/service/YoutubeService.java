package com.lit_map_BackEnd.domain.youtube.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lit_map_BackEnd.domain.youtube.entity.Youtube;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class YoutubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Youtube> getYoutubeInfo(String query) throws Exception {
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

            // Get view count from video details API
            String videoDetailsUrl = String.format(
                    "https://www.googleapis.com/youtube/v3/videos?part=statistics&id=%s&key=%s",
                    videoId, apiKey);
            String videoDetailsResponse = restTemplate.getForObject(videoDetailsUrl, String.class);
            JsonNode videoDetailsNode = objectMapper.readTree(videoDetailsResponse);
            long viewCount = videoDetailsNode.get("items").get(0).get("statistics").get("viewCount").asLong();

            // Create Youtube object
            Youtube youtube = new Youtube(title, "https://www.youtube.com/watch?v=" + videoId, thumbnailUrl, viewCount, uploadDate);

            youtubes.add(youtube);
        }

        return youtubes;
    }
}
