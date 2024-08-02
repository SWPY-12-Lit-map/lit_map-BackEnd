package com.lit_map_BackEnd.domain.admin.controller;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.common.util.SessionUtil;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.admin.service.AdminMemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMember;
    private final AdminMemberService adminMemberService;

    private void checkAdminRole(HttpServletRequest request) {
        Member currentUser = SessionUtil.getLoggedInUser(request);
        if (currentUser == null || currentUser.getMemberRoleStatus() != MemberRoleStatus.ADMIN) {
            throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
        }
    }

    @GetMapping("/mypage")
    @Operation(summary = "관리자 마이페이지", description = "관리자의 마이페이지를 조회합니다.")
    public ResponseEntity<SuccessResponse<Member>> getAdminMyPage(HttpServletRequest request) {
        try {
            checkAdminRole(request); // ADMIN 권한 확인
            Member profile = SessionUtil.getLoggedInUser(request);

            if (profile != null) {
                SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                        .result(profile)
                        .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                        .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                        .build();
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
            }
        } catch (BusinessExceptionHandler e) {
            return new ResponseEntity<>(new SuccessResponse<>(
                    null,
                    e.getErrorCode().getStatus(),
                    e.getErrorCode().getMessage()
            ), HttpStatus.valueOf(e.getErrorCode().getStatus()));
        }
    }

    @GetMapping("/all")
    @Operation(summary = "회원조회", description = "전체회원조회")
    public ResponseEntity<SuccessResponse<List<Member>>> getAllMembers(HttpServletRequest request) {
        checkAdminRole(request); // ADMIN 권한 확인
        List<Member> members = adminMember.getAllMembers(); // 모든 회원 정보 조회

        // 관리자 회원을 제외한 나머지 회원 상태만 필터링
        List<Member> filteredMembers = members.stream()
                .filter(member -> member.getMemberRoleStatus() != MemberRoleStatus.ADMIN)
                .filter(member -> member.getMemberRoleStatus() == MemberRoleStatus.ACTIVE_MEMBER ||
                        member.getMemberRoleStatus() == MemberRoleStatus.PENDING_MEMBER ||
                        member.getMemberRoleStatus() == MemberRoleStatus.UNKNOWN_MEMBER ||
                        member.getMemberRoleStatus() == MemberRoleStatus.WITHDRAWN_MEMBER ||
                        member.getMemberRoleStatus() == MemberRoleStatus.PUBLISHER_MEMBER)
                .toList();

        SuccessResponse<List<Member>> res = SuccessResponse.<List<Member>>builder()
                .result(filteredMembers) // 필터링된 회원 리스트
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/pending")
    @Operation(summary = "승인대기회원조회", description = "승인대기중인 회원조회")
    public ResponseEntity<SuccessResponse<List<Member>>> getPendingMembers(HttpServletRequest request) {
        checkAdminRole(request); // ADMIN 권한 확인
        List<Member> pendingMembers = adminMember.getMembersByStatus(MemberRoleStatus.PENDING_MEMBER); // 승인 대기 중인 회원 조회
        SuccessResponse<List<Member>> res = SuccessResponse.<List<Member>>builder()
                .result(pendingMembers) // 조회된 승인 대기 회원 리스트
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/approve/{memberId}")
    @Operation(summary = "회원승인", description = "회원승인")
    public ResponseEntity<SuccessResponse<Member>> approveMember(@PathVariable Long memberId, HttpServletRequest request) {
        checkAdminRole(request); // ADMIN 권한 확인
        Member approvedMember = adminMember.approveMember(memberId); // 회원 승인 처리
        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(approvedMember) // 승인된 회원 정보
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg("회원 승인 완료")
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/approve-withdrawal/{memberId}")
    @Operation(summary = "회원탈퇴승인", description = "회원탈퇴승인")
    public ResponseEntity<SuccessResponse<Void>> approveWithdrawal(@PathVariable Long memberId, HttpServletRequest request) {
        checkAdminRole(request); // ADMIN 권한 확인
        adminMemberService.approveWithdrawal(memberId); // 회원 탈퇴 승인 처리
        SuccessResponse<Void> res = SuccessResponse.<Void>builder()
                .result(null)
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg("회원 탈퇴 승인 완료")
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/force-withdraw/{memberId}")
    @Operation(summary = "회원강제탈퇴", description = "회원강제탈퇴")
    public ResponseEntity<SuccessResponse<Void>> forceWithdrawMember(@PathVariable Long memberId, HttpServletRequest request) {
        checkAdminRole(request); // ADMIN 권한 확인
        adminMember.forceWithdrawMember(memberId); // 회원 강제 탈퇴 처리
        SuccessResponse<Void> res = SuccessResponse.<Void>builder()
                .result(null)
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg("회원 강제 탈퇴 완료")
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
