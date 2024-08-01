package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.common.util.SessionUtil;
import com.lit_map_BackEnd.domain.member.dto.FindPublisherEmailDto;
import com.lit_map_BackEnd.domain.member.dto.PublisherDto;
import com.lit_map_BackEnd.domain.member.dto.PublisherUpdateDto;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final MemberPublisherService memberPublisherService;

    @PostMapping("/register")
    @Operation(summary = "출판사 회원가입", description = "새로운 출판사 회원을 등록합니다.")
    public ResponseEntity<SuccessResponse<PublisherDto>> registerPublisher(@RequestBody @Validated PublisherDto publisherDto, HttpServletRequest request, HttpServletResponse response) {
        PublisherDto savedPublisherDto = memberPublisherService.savePublisher(publisherDto);

        SuccessResponse<PublisherDto> res = SuccessResponse.<PublisherDto>builder()
                .result(savedPublisherDto)
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
    } // 회원가입시 유효성 체크

//    @GetMapping("/fetch")
//    @Operation(summary = "공공API를 사용하여 출판사 정보 가져오기", description = "공공API를 사용하여 출판사 정보를 가져옵니다.")
//    public ResponseEntity<SuccessResponse<Publisher>> fetchPublisherFromApi(@RequestBody Long publisherNumber) {
//        Publisher fetchedPublisher = memberPublisherService.fetchPublisherFromApi(publisherNumber);
//        SuccessResponse<Publisher> res = SuccessResponse.<Publisher>builder()
//                .result(fetchedPublisher)
//                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
//                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
//                .build();
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }

    @PostMapping("/find-email")
    @Operation(summary = "이메일 찾기", description = "사업자 번호, 출판사명, 회원이름을 사용하여 이메일을 찾습니다.")
    public ResponseEntity<SuccessResponse<String>> findEmail(@RequestBody FindPublisherEmailDto findPublisherEmailDto) {
        String foundEmail = memberPublisherService.findPublisherEmail(
                findPublisherEmailDto.getPublisherNumber(),
                findPublisherEmailDto.getPublisherName(),
                findPublisherEmailDto.getMemberName());

        SuccessResponse<String> res = SuccessResponse.<String>builder()
                .result(foundEmail)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/mypage")
    @Operation(summary = "출판사 직원 마이페이지 조회", description = "현재 로그인된 출판사 직원의 마이페이지를 조회합니다.")
    public ResponseEntity<SuccessResponse<Object>> getPublisherMyPage(HttpServletRequest request) {
        Member profile = SessionUtil.getLoggedInUser(request);

        if (profile != null && profile.getMemberRoleStatus() == MemberRoleStatus.PUBLISHER_MEMBER) {
            // 최신 정보를 가져와 세션을 업데이트합니다.
            Member updatedProfile = memberPublisherService.findByLitmapEmail(profile.getLitmapEmail());
            SessionUtil.setLoggedInUser(request, updatedProfile);

            PublisherDto publisherDto = (PublisherDto) request.getSession(false).getAttribute("publisherDto");
            SuccessResponse<Object> res = SuccessResponse.builder()
                    .result(publisherDto)
                    .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/update")
    @Operation(summary = "출판사 직원 정보 수정", description = "출판사 직원의 마이페이지 정보를 수정합니다.")
    public ResponseEntity<SuccessResponse<PublisherDto>> updatePublisher(@RequestBody @Validated PublisherUpdateDto publisherUpdateDto, HttpServletRequest request) {
        Member loggedMember = SessionUtil.getLoggedInUser(request);
        if (loggedMember == null || loggedMember.getPublisher() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 인증되지 않은 경우 401 응답
        }

        PublisherDto updatedPublisher = memberPublisherService.updatePublisherMember(loggedMember.getLitmapEmail(), publisherUpdateDto);

        // 세션 정보 업데이트
        Member updatedProfile = memberPublisherService.findByLitmapEmail(loggedMember.getLitmapEmail());
        SessionUtil.setLoggedInUser(request, updatedProfile);

        SuccessResponse<PublisherDto> res = SuccessResponse.<PublisherDto>builder()
                .result(updatedPublisher)
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg("Update successful")
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
