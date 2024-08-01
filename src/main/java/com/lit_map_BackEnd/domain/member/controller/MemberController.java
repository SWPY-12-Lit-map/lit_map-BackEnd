package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.common.util.SessionUtil;
import com.lit_map_BackEnd.domain.admin.service.AdminAuthService;
import com.lit_map_BackEnd.domain.admin.service.AdminMemberService;
import com.lit_map_BackEnd.domain.member.dto.*;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import com.lit_map_BackEnd.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
    private final MemberPublisherService memberPublisherService;
    private final MemberService memberService;
    private final SessionUtil sessionUtil;
    private final AdminMemberService adminMemberService;

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    public ResponseEntity<SuccessResponse<Member>> registerMember(@RequestBody @Validated MemberDto memberDto, HttpServletRequest request, HttpServletResponse response) {
        Member savedMember = memberPublisherService.saveMember(memberDto);

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(savedMember)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

//    @PostMapping("/{memberId}/approve")
//    @Operation(summary = "회원 승인", description = "관리자가 회원 가입을 승인합니다.")
//    public ResponseEntity<SuccessResponse<Member>> approveMember(@PathVariable Long memberId) {
//        Member approvedMember = memberPublisherService.approveMember(memberId);
//
//        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
//                .result(approvedMember)
//                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
//                .resultMsg("회원 가입이 승인되었습니다.")
//                .build();
//
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String litmapEmail, @RequestParam String workEmail) {
        boolean litmapEmailExists = memberPublisherService.checkLitmapEmailExists(litmapEmail);
        boolean workEmailExists = memberPublisherService.checkWorkEmailExists(workEmail);

        if (litmapEmailExists && workEmailExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("릿맵 이메일과 업무용 이메일이 이미 존재합니다.");
        } else if (litmapEmailExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 릿맵 이메일입니다.");
        } else if (workEmailExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 업무용 이메일입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 이메일입니다.");
        }
    }// 회원가입시 이메일 중복 체크

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "회원이 로그인합니다.")
    public ResponseEntity<SuccessResponse<Object>> login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Login attempt for email: " + loginDto.getLitmapEmail());

        try {
            // 회원 로그인 처리
            Member loggedMember = memberPublisherService.login(loginDto.getLitmapEmail(), loginDto.getPassword());
            if (loggedMember.getMemberRoleStatus() == MemberRoleStatus.WITHDRAWN_MEMBER) {
                throw new BusinessExceptionHandler(ErrorCode.WITHDRAWN_USER);
            }

            // 세션 쿠키 설정
            SessionUtil.createSessionCookie(request.getSession(false), response);
            SessionUtil.setLoggedInUser(request, loggedMember);

            logger.info("User logged in: " + loginDto.getLitmapEmail());
            logger.info("Session ID: " + request.getSession(false).getId());

            // 세션에 사용자 정보 저장
            SessionUtil.setLoggedInUser(request, loggedMember);

            SuccessResponse<Object> res = SuccessResponse.builder()
                    .result(loggedMember)
                    .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .build();

            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Login failed for email: " + loginDto.getLitmapEmail(), e);
            SuccessResponse<Object> res = SuccessResponse.builder()
                    .result(null)
                    .resultCode(ErrorCode.NOT_VALID_ERROR.getStatus())
                    .resultMsg("로그인 실패")
                    .build();
            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }
    }

//    @GetMapping("/mypage")
//    @Operation(summary = "마이페이지 조회", description = "현재 로그인된 사용자의 마이페이지를 조회합니다.")
//    public ResponseEntity<SuccessResponse<Object>> getMyPage(HttpServletRequest request) {
//        HttpSession session = request.getSession(false); // 현재 세션 가져오기
//        Member profile = SessionUtil.getLoggedInUser(session);
//
//        if (profile != null) {
//            Object result = session.getAttribute("publisherDto") != null ? session.getAttribute("publisherDto") : profile;
//            SuccessResponse<Object> res = SuccessResponse.builder()
//                    .result(result)
//                    .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
//                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
//                    .build();
//            return new ResponseEntity<>(res, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }

    @PostMapping("/verify-password")
    @Operation(summary = "비밀번호 확인", description = "마이페이지 접근 전에 비밀번호를 확인합니다.")
    public ResponseEntity<SuccessResponse<Boolean>> verifyPassword(@RequestBody LoginDto loginDto) {
        Member member = memberPublisherService.verifyPassword(loginDto.getLitmapEmail(), loginDto.getPassword());

        boolean isVerified = member != null;

        String message;
        if (isVerified) {
            if (member.getMemberRoleStatus() == MemberRoleStatus.ACTIVE_MEMBER) {
                message = "비밀번호 확인 성공. 1인회원 마이페이지로 이동";
            } else if (member.getMemberRoleStatus() == MemberRoleStatus.PUBLISHER_MEMBER) {
                message = "비밀번호 확인 성공. 출판사 직원 마이페이지로 이동";
            } else {
                message = "비밀번호 확인 성공. 마이페이지로 이동";
            }
        } else {
            message = "비밀번호 확인 실패";
        }

        SuccessResponse<Boolean> res = SuccessResponse.<Boolean>builder()
                .result(isVerified)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(message)
                .build();

        return new ResponseEntity<>(res, isVerified ? HttpStatus.OK : HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/mypage")
    @Operation(summary = "1인작가 마이페이지 조회", description = "현재 로그인된 1인작가의 마이페이지를 조회합니다.")
    public ResponseEntity<SuccessResponse<Object>> getMemberMyPage(HttpServletRequest request) {
        Member profile = SessionUtil.getLoggedInUser(request);

        System.out.println(profile.getLitmapEmail());
        if (profile != null && profile.getMemberRoleStatus() == MemberRoleStatus.ACTIVE_MEMBER) {
            // 최신 정보를 가져와 세션을 업데이트합니다.
            Member updatedProfile = memberPublisherService.findByLitmapEmail(profile.getLitmapEmail());
            SessionUtil.setLoggedInUser(request, updatedProfile);

            SuccessResponse<Object> res = SuccessResponse.builder()
                    .result(updatedProfile)
                    .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            throw new BusinessExceptionHandler(ErrorCode.PENDING_USER);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "1인작가 정보 수정", description = "1인작가의 마이페이지 정보를 수정")
    public ResponseEntity<SuccessResponse<Member>> updateMember(@RequestBody @Validated MemberUpdateDto memberUpdateDto, HttpServletRequest request) {
        Member loggedMember = SessionUtil.getLoggedInUser(request);
        if (loggedMember == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 인증되지 않은 경우 401 응답
        }

        Member updatedMember = memberPublisherService.updateMember(loggedMember.getLitmapEmail(), memberUpdateDto);

        // 세션 정보 업데이트
        SessionUtil.setLoggedInUser(request, updatedMember);

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(updatedMember)
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg("Update successful")
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/{memberId}/request-withdrawal")
    @Operation(summary = "작가,직원 탈퇴 요청", description = "작가,직원 탈퇴를 요청")
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
    @Operation(summary = "작가,직원 탈퇴 승인", description = "작가,직원 탈퇴 승인")
    public ResponseEntity<SuccessResponse<String>> approveMemberWithdrawal(@PathVariable Long memberId) {
        adminMemberService.approveWithdrawal(memberId);
        SuccessResponse<String> res = SuccessResponse.<String>builder()
                .result("회원 탈퇴가 승인되었습니다.")
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
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
}
