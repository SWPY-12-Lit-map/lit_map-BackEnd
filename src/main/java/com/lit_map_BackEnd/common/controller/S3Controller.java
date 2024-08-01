package com.lit_map_BackEnd.common.controller;

import com.lit_map_BackEnd.common.entity.ImageRequestDto;
import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.common.service.S3Service;
import com.lit_map_BackEnd.common.util.SessionUtil;
import com.lit_map_BackEnd.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping("")
    @Operation(summary = "이미지 파일 미리 저장", description = "이미지 파일 미리 저장하기")
    public ResponseEntity<SuccessResponse> uploadImage(@RequestPart MultipartFile image,
                                                       @RequestPart String path) throws IOException {
        String imageUrl = s3Service.uploadImage(image, path);

        SuccessResponse res = SuccessResponse.builder()
                .result(imageUrl)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("")
    @Operation(summary = "저장된 이미지 삭제", description = "S3에 저장된 이미지 파일 삭제")
    public ResponseEntity<SuccessResponse> deleteImage(HttpServletRequest request,
                                                       @RequestBody ImageRequestDto imageRequestDto) {
        Member loggedInUser = SessionUtil.getLoggedInUser(request);
        s3Service.deleteImage(loggedInUser, imageRequestDto.getImageUrl());

        SuccessResponse res = SuccessResponse.builder()
                .result("이미지가 삭제되었습니다")
                .resultCode(SuccessCode.DELETE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.DELETE_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("")
    @Operation(summary = "배너 사진 가져오기", description = "배너 사진 가져오는 API")
    public ResponseEntity<SuccessResponse> getBannerPhotos(HttpServletRequest request) {
        Member loggedInUser = SessionUtil.getLoggedInUser(request);
        List<String> bannerImages = s3Service.getBannerImages(loggedInUser);

        SuccessResponse res = SuccessResponse.builder()
                .result(bannerImages)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
