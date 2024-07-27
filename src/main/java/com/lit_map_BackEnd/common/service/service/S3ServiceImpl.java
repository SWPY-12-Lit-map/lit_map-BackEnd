package com.lit_map_BackEnd.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.lit_map_BackEnd.common.service.S3Service;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.name}")
    private String cfName;

    private static final String EXTENSION_DELIMITER = ".";
    private static final List<String> ALLOWED_FILE_EXTENSIONS = List.of(".jpg", ".png", ".jpeg");

    @Override
    public String uploadImage(MultipartFile multipartFile, String path) throws IOException {
       // 해당 파일이 jpg, png, jpeg가 아니라면 예외처리
        String fileExtension = extractFileExtension(multipartFile);

        // UUID를 새로운 이름에 추가해서 유일한 이름으로 제작
        String uniqueName = UUID.randomUUID().toString().concat(fileExtension);

        // 원하는 폴더에 넣기 위해 path를 포함한 저장 이름 필요
        String pathFile = path + "/" + uniqueName;
        // s3에 등록할 파일을 만들어주기 위해 서버에 MultipartFile을 File객체로 변환한다
        File uploadFile = convert(multipartFile);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        // 실제로 S3로 파일을 저장하고 실제 url의 위치를 반환해주는 메소드
        String uploadImageUrl = putS3(uploadFile, pathFile);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    @Override
    public void deleteImage(String name) {
        // URI가 전체로 들어오기 때문에 뒤에 path 부분만 남겨서 확인을 해야한다.
        String path = URI.create(name).getPath();
        String key = path.substring(path.indexOf("/") + 1);

        boolean isExist = amazonS3.doesObjectExist(bucket, key);
        if (isExist) {
            try {
                String decodedFileName = URLDecoder.decode(key, "UTF-8");
                amazonS3.deleteObject(bucket, decodedFileName);
            } catch (UnsupportedEncodingException e) {
                throw new BusinessExceptionHandler(ErrorCode.ENCODING_ERROR);
            }
        }
    }

    private File convert(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String uniqueFile = uuid + "_" + originalName.replace("\\s", "_");

        File convertFile = new File(uniqueFile);
        // 파일이 성공적으로 생성되어야 변환을 시작한다.
        if (convertFile.createNewFile()) {
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(convertFile))) {
                bos.write(file.getBytes());
            } catch (IOException e) {
                log.error("파일 변환 중 오류 발생 : {}", e.getMessage());
                throw e;
            }
            return convertFile;
        }
        throw new BusinessExceptionHandler(ErrorCode.FILE_CONVERT_ERROR);
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
        String path = amazonS3.getUrl(bucket, fileName).getPath();
        return cfName + path;
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) log.info("파일이 성공적으로 삭제되었습니다");
        else log.info("파일 삭제를 실패하였습니다");
    }

    private String extractFileExtension(MultipartFile file) {
        // 파일 이름에서 공백 제거 한 새로운 이름
        String originalFileName = file.getOriginalFilename();

        // 파일의 마지막 점의 위치를 잘라 파일 확장자 명 확인
        // -> 확장자 명이 없다면 NPE 발생
        int extensionIndex = Objects.requireNonNull(originalFileName).indexOf(EXTENSION_DELIMITER);

        // '.'이 없거나 허락된 파일 확장자 명을 가지고 있는지 확인한다.
        // 없으면 확장자 불가 판단
        if (extensionIndex == -1 || !ALLOWED_FILE_EXTENSIONS.contains(
                originalFileName.substring(extensionIndex))) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_FILE_FORMAT);
        }
        return originalFileName.substring(extensionIndex);
    }
}