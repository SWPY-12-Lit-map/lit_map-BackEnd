package com.lit_map_BackEnd.common.service;

import com.lit_map_BackEnd.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface S3Service {
    String uploadImage(MultipartFile image, String path) throws IOException;

    void deleteImage(Member member, String name);

    List<String> getBannerImages(Member member);
}