package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.member.dto.*;
import com.lit_map_BackEnd.domain.member.entity.CustomUserDetails;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import com.lit_map_BackEnd.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    public ResponseEntity<SuccessResponse<Member>> registerMember(@RequestBody @Validated MemberDto memberDto) {
        Member savedMember = memberPublisherService.saveMember(memberDto);

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(savedMember)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/{memberId}/approve")
    @Operation(summary = "회원 승인", description = "관리자가 회원 가입을 승인합니다.")
    public ResponseEntity<SuccessResponse<Member>> approveMember(@PathVariable Long memberId) {
        Member approvedMember = memberPublisherService.approveMember(memberId);

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(approvedMember)
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg("회원 가입이 승인되었습니다.")
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
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

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "회원이 로그인합니다.")
    public ResponseEntity<SuccessResponse<Member>> login(@RequestBody LoginDto loginDto, HttpSession session) {
        Member loggedMember = memberPublisherService.login(loginDto.getLitmapEmail(), loginDto.getPassword());
        session.setAttribute("loggedInUser", new CustomUserDetails(loggedMember)); // 세션에 로그인된 사용자 정보 저장

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(loggedMember)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자를 로그아웃하고 세션을 무효화합니다.")
    public ResponseEntity<SuccessResponse<String>> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        SuccessResponse<String> res = SuccessResponse.<String>builder()
                .result("로그아웃 되었습니다.")
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/find-email")
    @Operation(summary = "이메일 찾기", description = "업무용 이메일과 이름을 사용하여 이메일을 찾습니다.")
    public ResponseEntity<SuccessResponse<String>> findEmail(@RequestBody FindEmailDto findEmailDto) {
        String foundEmail = memberPublisherService.findMemberEmail(findEmailDto.getWorkEmail(), findEmailDto.getName());
        SuccessResponse<String> res = SuccessResponse.<String>builder()
                .result(foundEmail)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update")
    @Operation(summary = "회원 정보 수정", description = "현재 로그인된 사용자의 정보를 수정합니다.")
    public ResponseEntity<SuccessResponse<Member>> updateMember(HttpSession session, @RequestBody @Validated MemberUpdateDto memberUpdateDto) {
        CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("loggedInUser");
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String litmapEmail = userDetails.getUsername(); // 세션에서 가져온 사용자 이메일
        Member updatedMember = memberPublisherService.updateMember(litmapEmail, memberUpdateDto);

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(updatedMember)
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg("Update successful")
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/profile")
    @Operation(summary = "회원 프로필 조회", description = "현재 로그인된 사용자의 프로필을 조회합니다.")
    public ResponseEntity<SuccessResponse<Member>> getProfile(HttpSession session) {
        CustomUserDetails userDetails = (CustomUserDetails) session.getAttribute("loggedInUser");
        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String litmapEmail = userDetails.getUsername();
        Member memberProfile = memberPublisherService.findByLitmapEmail(litmapEmail);

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(memberProfile)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{memberId}/request-withdrawal")
    @Operation(summary = "회원 탈퇴 요청", description = "회원의 탈퇴를 요청합니다.")
    public ResponseEntity<SuccessResponse<String>> requestMemberWithdrawal(@PathVariable Long memberId) {
        memberService.requestWithdrawal(memberId);
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
        memberService.approveWithdrawal(memberId);
        SuccessResponse<String> res = SuccessResponse.<String>builder()
                .result("회원 탈퇴가 승인되었습니다.")
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
