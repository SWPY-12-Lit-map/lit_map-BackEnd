package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.common.util.SessionUtil;
import com.lit_map_BackEnd.domain.member.dto.*;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import com.lit_map_BackEnd.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
    private final MemberPublisherService memberPublisherService;
    private final MemberService memberService;
    private final SessionUtil sessionUtil;

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
    public ResponseEntity<SuccessResponse<Object>> login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Login attempt for email: " + loginDto.getLitmapEmail());

        try {
            Member loggedMember = memberPublisherService.login(loginDto.getLitmapEmail(), loginDto.getPassword());
            if (loggedMember.getMemberRoleStatus() == MemberRoleStatus.WITHDRAWN_MEMBER) {
                throw new BusinessExceptionHandler(ErrorCode.WITHDRAWN_USER);
            }

            SessionUtil.createSessionCookie(request.getSession(false), response);
            SessionUtil.setLoggedInUser(request, loggedMember);

            logger.info("User logged in: " + loginDto.getLitmapEmail());
            logger.info("Session ID: " + request.getSession(false).getId());

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

    @PostMapping("/verify-password")
    @Operation(summary = "비밀번호 확인", description = "마이페이지 접근 전에 비밀번호를 확인합니다.")
    public ResponseEntity<SuccessResponse<Boolean>> verifyPassword(HttpServletRequest request,
                                                                   @RequestBody CheckPassword password) {
        Member loggedInUser = SessionUtil.getLoggedInUser(request);
        if (loggedInUser == null) {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
        boolean isVerified = memberPublisherService.verifyPassword(loggedInUser.getPassword(), password.getPassword());

        String message;
        if (isVerified) {
            if (loggedInUser.getMemberRoleStatus() == MemberRoleStatus.ACTIVE_MEMBER) {
                message = "비밀번호 확인 성공. 1인회원 마이페이지로 이동";
            } else if (loggedInUser.getMemberRoleStatus() == MemberRoleStatus.PUBLISHER_MEMBER) {
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

        if (profile != null) {
            Member updatedProfile = memberPublisherService.findByLitmapEmail(profile.getLitmapEmail());
            SessionUtil.setLoggedInUser(request, updatedProfile);

            if (updatedProfile.getMemberRoleStatus() == MemberRoleStatus.ACTIVE_MEMBER) {
                SuccessResponse<Object> res = SuccessResponse.builder()
                        .result(updatedProfile)
                        .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                        .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                        .build();
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else if (updatedProfile.getMemberRoleStatus() == MemberRoleStatus.PENDING_MEMBER) {
                throw new BusinessExceptionHandler(ErrorCode.PENDING_USER);
            } else {
                throw new BusinessExceptionHandler(ErrorCode.INVALID_USER_INFO);
            }
        } else {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
    }

    @PutMapping("/profile/update")
    @Operation(summary = "프로필 정보 수정", description = "메세지, 닉네임, 프로필이미지")
    public ResponseEntity<SuccessResponse<Member>> updateProfile(@RequestBody @Validated ProfileUpdateDto profileUpdateDto, HttpServletRequest request) {
        Member loggedMember = SessionUtil.getLoggedInUser(request);
        if (loggedMember == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Member updatedMember = memberPublisherService.updateProfile(loggedMember.getLitmapEmail(), profileUpdateDto);
        SessionUtil.setLoggedInUser(request, updatedMember);

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(updatedMember)
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg("Profile update successful")
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update")
    @Operation(summary = "1인작가 정보 수정", description = "1인작가의 마이페이지 정보를 수정")
    public ResponseEntity<SuccessResponse<Member>> updateMember(@RequestBody @Validated MemberUpdateDto memberUpdateDto, HttpServletRequest request) {
        Member loggedMember = SessionUtil.getLoggedInUser(request);
        if (loggedMember == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Member updatedMember = memberPublisherService.updateMember(loggedMember.getLitmapEmail(), memberUpdateDto);
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

    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자를 로그아웃하고 세션을 무효화합니다.")
    public ResponseEntity<SuccessResponse<String>> logout(HttpSession session) {
        session.invalidate();
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
