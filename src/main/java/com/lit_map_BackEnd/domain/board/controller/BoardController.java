package com.lit_map_BackEnd.domain.board.controller;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.common.util.SessionUtil;
import com.lit_map_BackEnd.domain.board.dto.*;
import com.lit_map_BackEnd.domain.board.service.BoardService;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    // 승인 대기 중인 작품 나열
    @GetMapping("/confirm")
    @Operation(summary = "승인 대기 목록", description = "승인 대기 중 작품들 작품-버전List로 데이터 가져오기")
    public ResponseEntity<SuccessResponse> getConfirmVersion(HttpServletRequest request) {
        Member loggedInUser = SessionUtil.getLoggedInUser(request);
        if (loggedInUser.getMemberRoleStatus() == MemberRoleStatus.ADMIN) {
            List<WorkResponseDto> confirmData = boardService.getConfirmData();

            SuccessResponse res = SuccessResponse.builder()
                    .result(confirmData)
                    .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .build();

            return new ResponseEntity<>(res, HttpStatus.OK);
        } else throw new BusinessExceptionHandler(ErrorCode.FORBIDDEN_ERROR);
    }

    // 내가 작성한 작품 목록
    @GetMapping("/myWorkList")
    @Operation(summary = "나의 작품 목록", description = "내가 등록한 작품-버전 list로 가져오기")
    public ResponseEntity<SuccessResponse> getMyWorkList(HttpServletRequest request) {
        Member loggedInUser = SessionUtil.getLoggedInUser(request);
        MyWorkListResponseDto myWorkList = boardService.getMyWorkList(loggedInUser.getId());

        SuccessResponse res = SuccessResponse.builder()
                .result(myWorkList)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    // view 순으로 작품 나열 ( Redis 에 저장해서 사용 )
    @GetMapping("/view")
    @Operation(summary = "view 순으로 나열된 작품 목록", description = "view 순으로 나열된 작품 목록 나열")
    public ResponseEntity<SuccessResponse> getWorkByView(@RequestParam(name = "pn") int pn) {

        Slice<WorkResponseDto> workListByView = boardService.getWorkListByView(pn);

        SuccessResponse res = SuccessResponse.builder()
                .result(workListByView)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // 업데이트 순으로 나열 ( Redis 에 저장해서 사용 )
    @GetMapping("/updateList")
    @Operation(summary = "update 기준으로 나열하는 작품 목록", description = "update 기준으로 나열")
    public ResponseEntity<SuccessResponse> getWorkByUpdateDate(@RequestParam(name = "pn") int pn) {

        Slice<WorkResponseDto> workListByView = boardService.getWorkListByUpdateDate(pn);

        SuccessResponse res = SuccessResponse.builder()
                .result(workListByView)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/theme/{categoryName}")
    @Operation(summary = "카테고리별 전체 작품 가져오기", description = "카테고리를 기준으로 작품 정렬")
    public ResponseEntity<SuccessResponse> getWorkByCategory(@PathVariable(name = "categoryName") String categoryName,
                                                             @RequestParam(name = "pn")int pn) {

        Slice<WorkResponseDto> workByCategory = boardService.getWorkByCategory(pn, categoryName);

        SuccessResponse res = SuccessResponse.builder()
                .result(workByCategory)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // 카테고리와 장르를 기준으로 작품 검색
    @GetMapping("/theme/{categoryId}/{genreId}")
    @Operation(summary = "카테고리와 장르별 작품 가져오기", description = "카테고리와 장르를 이름을 기준으로 작품들을 가져오기")
    public ResponseEntity<SuccessResponse> getWorkByCategoryAndGenre(@PathVariable(name = "categoryId") Long categoryId,
                                                               @PathVariable(name = "genreId") Long genreId) {

        List<Map<String, Object>> list = boardService.getWorkByCategoryAndGenre(categoryId, genreId);

        SuccessResponse res = SuccessResponse.builder()
                .result(list)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // 각 특징을 통해 작품을 검색해서 나열
    @PostMapping("/search")
    @Operation(summary = "검색", description = "각 타입과 내용으로 검색하기")
    public ResponseEntity<SuccessResponse> getWorksByQuestion(@RequestBody SearchDto searchDto) {
        Map<String, CategoryResultDto> worksBySearch = boardService.findWorksBySearch(searchDto);

        SuccessResponse res = SuccessResponse.builder()
                .result(worksBySearch)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/workCount")
    @Operation(summary = "작품 갯수", description = "나의 완성 작품과 미완성 작품 갯수 가져오기")
    public ResponseEntity<SuccessResponse> getWorkCount(HttpServletRequest request) {
        Member loggedInUser = SessionUtil.getLoggedInUser(request);
        Map<String, Long> worksCount = boardService.getWorksCount(loggedInUser.getId());

        SuccessResponse res = SuccessResponse.builder()
                .result(worksCount)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/banner")
    @Operation(summary = "배너 가져오기", description = "관리자가 지정해놓은 배너 사진 가져오기")
    public ResponseEntity<SuccessResponse> getBannerImage() {
        List<String> bannerImages = boardService.getBannerImages();

        SuccessResponse res = SuccessResponse.builder()
                .result(bannerImages)
                .resultCode(SuccessCode.SELECT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
