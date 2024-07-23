package com.lit_map_BackEnd.domain.youtube.entity;


import lombok.*;

//getter setter 로 하면 오류

 @Getter
 @Setter
 @Builder
public class Youtube {
    private String title;
    private String videoUrl;
    private String thumbnailUrl;
    private long viewCount;
    private String uploadDate;

    public Youtube(String title, String videoUrl, String thumbnailUrl, long viewCount, String uploadDate) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.viewCount = viewCount;
        this.uploadDate = uploadDate;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public long getViewCount() {
        return viewCount;
    }

    public String getUploadDate() {
        return uploadDate;
    }
}
