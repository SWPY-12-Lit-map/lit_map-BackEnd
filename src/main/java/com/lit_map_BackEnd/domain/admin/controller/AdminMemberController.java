package com.lit_map_BackEnd.domain.admin.controller;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.admin.service.AdminMemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMember;
    private final AdminMemberService adminMemberService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한을 가진 사용자만 접근 가능
    @Operation(summary = "모든 회원 정보 조회", description = "모든 회원의 정보를 조회합니다.")
    public ResponseEntity<SuccessResponse<List<Member>>> getAllMembers() {
        List<Member> members = adminMember.getAllMembers(); // 모든 회원 정보 조회
        SuccessResponse<List<Member>> res = SuccessResponse.<List<Member>>builder()
                .result(members) // 조회된 회원 리스트
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한을 가진 사용자만 접근 가능
    @Operation(summary = "승인 대기 회원 조회", description = "승인 대기 중인 회원을 조회합니다.")
    public ResponseEntity<SuccessResponse<List<Member>>> getPendingMembers() {
        List<Member> pendingMembers = adminMember.getMembersByStatus(MemberRoleStatus.PENDING_MEMBER); // 승인 대기 중인 회원 조회
        SuccessResponse<List<Member>> res = SuccessResponse.<List<Member>>builder()
                .result(pendingMembers) // 조회된 승인 대기 회원 리스트
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @PutMapping("/approve/{memberId}")
    @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한을 가진 사용자만 접근 가능
    @Operation(summary = "회원 승인", description = "회원 가입을 승인합니다.")
    public ResponseEntity<SuccessResponse<Member>> approveMember(@PathVariable("memberId") Long memberId) {

            Member member = adminMemberService.approveMember(memberId);
            SuccessResponse<Member> response = SuccessResponse.<Member>builder()
                    .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .result(member)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);

    }

}
