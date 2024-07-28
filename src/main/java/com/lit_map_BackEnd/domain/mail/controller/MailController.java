package com.lit_map_BackEnd.domain.mail.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.mail.dto.MailDto;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/email")
@RequiredArgsConstructor
public class MailController {

    private final MemberPublisherService memberPublisherService;

    @PostMapping("/find-password")
    @Operation(summary = "비밀번호 찾기", description = "회원의 이메일을 사용하여 임시 비밀번호를 발급합니다.")
    public ResponseEntity<SuccessResponse<String>> findPassword(@RequestBody @Validated MailDto findPwMailDto) {
        //    public ResponseEntity<SuccessResponse> findPassword(Long versionId, @RequestParam String summary) {
        try {
            String result = memberPublisherService.findPw(findPwMailDto);
            SuccessResponse<String> res = SuccessResponse.<String>builder()
                    .result(result)
                    .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new SuccessResponse<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
