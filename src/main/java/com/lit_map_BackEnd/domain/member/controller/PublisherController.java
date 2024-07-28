package com.lit_map_BackEnd.domain.member.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.common.util.SessionUtil;
import com.lit_map_BackEnd.domain.member.dto.FindPublisherEmailDto;
import com.lit_map_BackEnd.domain.member.dto.PublisherDto;
import com.lit_map_BackEnd.domain.member.dto.PublisherUpdateDto;
import com.lit_map_BackEnd.domain.member.entity.CustomUserDetails;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.Publisher;
import com.lit_map_BackEnd.domain.member.service.MemberPublisherService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final MemberPublisherService memberPublisherService;

    @PostMapping("/register")
    @Operation(summary = "출판사 회원가입", description = "새로운 출판사 회원을 등록합니다.")
    public ResponseEntity<SuccessResponse<Publisher>> registerPublisher(@RequestBody @Validated PublisherDto publisherDto, HttpServletRequest request, HttpServletResponse response) {
        Publisher savedPublisher = memberPublisherService.savePublisher(publisherDto);

        CustomUserDetails userDetails = new CustomUserDetails(savedPublisher.getMemberList().get(0));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        // 세션 쿠키 설정
        SessionUtil.createSessionCookie(session, response);

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
    } // 회원가입시 유효성 체크

    @GetMapping("/fetch")
    @Operation(summary = "공공API를 사용하여 출판사 정보 가져오기", description = "공공API를 사용하여 출판사 정보를 가져옵니다.")
    public ResponseEntity<SuccessResponse<Publisher>> fetchPublisherFromApi(@RequestBody Long publisherNumber) {
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

    @PutMapping("/update")
    @Operation(summary = "출판사 직원 정보 수정", description = "출판사 직원의 마이페이지 정보를 수정합니다.")
    public ResponseEntity<SuccessResponse<Member>> updatePublisher(@RequestBody @Validated PublisherUpdateDto publisherUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        String litmapEmail = userDetails.getUsername();
        Member updatedPublisher = memberPublisherService.updatePublisherMember(litmapEmail, publisherUpdateDto);

        SuccessResponse<Member> res = SuccessResponse.<Member>builder()
                .result(updatedPublisher)
                .resultCode(SuccessCode.UPDATE_SUCCESS.getStatus())
                .resultMsg("Update successful")
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}