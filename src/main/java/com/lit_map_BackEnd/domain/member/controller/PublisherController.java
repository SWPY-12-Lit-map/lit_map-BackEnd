package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.member.dto.PublisherDto;
import com.lit_map_BackEnd.domain.member.dto.PublisherUpdateDto;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.Publisher;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final MemberPublisherService memberPublisherService;
    private final HttpSession session;

    @PostMapping("/register")
    @Operation(summary = "출판사 회원가입", description = "새로운 출판사 회원을 등록합니다.")
    public ResponseEntity<SuccessResponse<Publisher>> registerPublisher(@RequestBody @Validated PublisherDto publisherDto) {
        Publisher savedPublisher = memberPublisherService.savePublisher(publisherDto);
        SuccessResponse<Publisher> res = SuccessResponse.<Publisher>builder()
                .result(savedPublisher)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String litmapEmail) {
        boolean exists = memberPublisherService.checkLitmapEmailExists(litmapEmail);
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 이메일입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 이메일입니다.");
        }
    }

    @GetMapping("/fetch")
    @Operation(summary = "공공API를 사용하여 출판사 정보 가져오기", description = "공공API를 사용하여 출판사 정보를 가져옵니다.")
    public ResponseEntity<SuccessResponse<Publisher>> fetchPublisherFromApi(@RequestParam Long publisherNumber) {
        Publisher fetchedPublisher = memberPublisherService.fetchPublisherFromApi(publisherNumber);
        SuccessResponse<Publisher> res = SuccessResponse.<Publisher>builder()
                .result(fetchedPublisher)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/find-email")
    @Operation(summary = "이메일 찾기", description = "사업자 번호, 출판사명, 이름을 사용하여 이메일을 찾습니다.")
    public ResponseEntity<SuccessResponse<String>> findEmail(@RequestParam Long publisherNumber, @RequestParam String publisherName, @RequestParam String memberName) {
        String foundEmail = memberPublisherService.findPublisherEmail(publisherNumber, publisherName, memberName);

        SuccessResponse<String> res = SuccessResponse.<String>builder()
                .result(foundEmail)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update")
    @Operation(summary = "출판사 직원 마이페이지 수정", description = "출판사 직원의 마이페이지 정보를 수정합니다.")
    public ResponseEntity<SuccessResponse<Member>> updatePublisher(@AuthenticationPrincipal User user, @RequestBody @Validated PublisherUpdateDto publisherUpdateDto) {
        Member updatedPublisher = memberPublisherService.updatePublisherMember(user.getUsername(), publisherUpdateDto);
        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(updatedPublisher)
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
