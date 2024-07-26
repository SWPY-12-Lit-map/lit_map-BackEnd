package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.member.dto.MemberDto;
import com.lit_map_BackEnd.domain.member.dto.MemberUpdateDto;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import com.lit_map_BackEnd.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberPublisherService memberPublisherService;
    private final MemberService memberService;
    private final HttpSession session;

    @PostMapping("/register")
    @Operation(summary = "1인작가 회원가입", description = "새로운 1인작가 회원을 등록합니다.")
    public ResponseEntity<SuccessResponse<Member>> registerMember(@RequestBody @Validated MemberDto memberDto) {
        Member savedMember = memberPublisherService.saveMember(memberDto);

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(savedMember)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "1인작가 및 출판사 회원이 로그인합니다.")
    public ResponseEntity<SuccessResponse<Member>> login(@RequestParam String litmapEmail, @RequestParam String password) {
        Member loggedMember = memberPublisherService.login(litmapEmail, password);

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(loggedMember)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자를 로그아웃하고 세션을 무효화합니다.")
    public ResponseEntity<SuccessResponse<String>> logout() {
        memberPublisherService.logout();
        SuccessResponse<String> res = SuccessResponse.<String>builder()
                .result("로그아웃 되었습니다.")
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/find-email")
    @Operation(summary = "이메일 찾기", description = "업무용 이메일과 이름을 사용하여 이메일을 찾습니다.")
    public ResponseEntity<SuccessResponse<String>> findEmail(@RequestParam String workEmail, @RequestParam String name) {
        String foundEmail = memberPublisherService.findMemberEmail(workEmail, name);
        SuccessResponse<String> res = SuccessResponse.<String>builder()
                .result(foundEmail)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update")
    @Operation(summary = "1인작가 마이페이지 수정", description = "1인작가의 마이페이지 정보를 수정합니다.")
    public ResponseEntity<SuccessResponse<Member>> updateMember(@AuthenticationPrincipal User user, @RequestBody @Validated MemberUpdateDto memberUpdateDto) {
        Member updatedMember = memberPublisherService.updateMember(user.getUsername(), memberUpdateDto);
        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(updatedMember)
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{memberId}/request-withdrawal")
    @Operation(summary = "회원 탈퇴 요청", description = "회원의 탈퇴를 요청합니다.")
    public ResponseEntity<SuccessResponse<String>> requestMemberWithdrawal(@PathVariable Long memberId) {
        memberService.requestMemberWithdrawal(memberId);
        SuccessResponse<String> res = SuccessResponse.<String>builder()
                .result("회원 탈퇴 요청이 완료되었습니다.")
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{memberId}/approve-withdrawal")
    @Operation(summary = "회원 탈퇴 승인", description = "회원의 탈퇴를 승인합니다.")
    public ResponseEntity<SuccessResponse<String>> approveMemberWithdrawal(@PathVariable Long memberId) {
        memberService.approveMemberWithdrawal(memberId);
        SuccessResponse<String> res = SuccessResponse.<String>builder()
                .result("회원 탈퇴가 승인되었습니다.")
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
