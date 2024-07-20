package com.lit_map_BackEnd.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    String uploadImage(MultipartFile image, String path) throws IOException;

    void deleteImage(String name);
}