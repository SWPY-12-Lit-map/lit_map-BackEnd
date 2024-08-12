package com.lit_map_BackEnd.common.service;

import com.lit_map_BackEnd.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    String uploadImage(MultipartFile image,Member member, String path) throws IOException;

    void deleteImage(Member member, String name);
}