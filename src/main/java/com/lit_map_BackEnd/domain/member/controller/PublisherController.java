package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.member.dto.MemberDto;
import com.lit_map_BackEnd.domain.member.dto.PublisherDto;
import com.lit_map_BackEnd.domain.member.dto.PublisherMemberRequestDto;
import com.lit_map_BackEnd.domain.member.entity.Publisher;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import com.lit_map_BackEnd.domain.member.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;
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

//    @DeleteMapping("/request-withdrawal-specific")
//    @Operation(summary = "출판사 탈퇴 요청 (특정 회원)", description = "출판사 탈퇴 요청을 처리하고 특정 회원에게만 탈퇴 메일을 전송합니다.")
//    public ResponseEntity<SuccessResponse<String>> requestWithdrawalSpecific(@RequestParam Long publisherId, @RequestParam Long memberId) {
//        publisherService.requestWithdrawalSpecific(publisherId, memberId);
//        SuccessResponse<String> res = SuccessResponse.<String>builder()
//                .result("탈퇴 요청이 접수되었습니다. 관리자의 승인이 필요합니다.")
//                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
//                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
//                .build();
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }

//    @PostMapping("/approve-withdrawal")
//    @Operation(summary = "출판사 탈퇴 승인", description = "관리자가 출판사 탈퇴를 승인합니다.")
//    public ResponseEntity<SuccessResponse<String>> approveWithdrawal(@RequestParam Long publisherId, @RequestParam(required = false) Long memberId) {
//        if (memberId != null) {
//            publisherService.approveWithdrawalSpecific(publisherId, memberId);
//        } else {
//            publisherService.approveWithdrawalAll(publisherId);
//        }
//        SuccessResponse<String> res = SuccessResponse.<String>builder()
//                .result("탈퇴 요청이 승인되었습니다.")
//                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
//                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
//                .build();
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }
}
