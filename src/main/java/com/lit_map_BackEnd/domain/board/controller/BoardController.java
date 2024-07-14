package com.lit_map_BackEnd.domain.board.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.board.dto.ConfirmListDto;
import com.lit_map_BackEnd.domain.board.service.BoardService;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    // 승인 대기 중인 작품 나열
    @GetMapping("/confirm")
    @Operation(summary = "승인 대기 목록", description = "승인 대기 중 작품들 작품-버전List로 데이터 가져오기")
    public ResponseEntity<SuccessResponse> getConfirmVersion() {

        List<ConfirmListDto> confirmData = boardService.getConfirmData();

        SuccessResponse res = SuccessResponse.builder()
                .result(confirmData)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/myWorkList")
    @Operation(summary = "나의 작품 목록", description = "내가 등록한 작품-버전list로 가져오기")
    public ResponseEntity<SuccessResponse> getMyWorkList() {
        // 멤버 정보 가져오기
        List<WorkResponseDto> myWorkList = boardService.getMyWorkList();

        // 멤버 아이디로 작품 가져오기
        SuccessResponse res = SuccessResponse.builder()
                .result(myWorkList)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    // view 순으로 작품 나열 ( Redis 에 저장해서 사용 )
    @GetMapping("/view")
    @Operation(summary = "Redis에 저장된 내용 전달", description = "Sorted Set으로 저장된 데이터를 나열")
    public ResponseEntity<SuccessResponse> getWorkByView() {


        SuccessResponse res = SuccessResponse.builder()
                .result(1)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // 업데이트 순으로 나열 ( Redis 에 저장해서 사용 )

    // 각 특징을 통해 작품을 검색해서 나열
    // 1. 제목

    // 2. 내용

    // 3. 제목 + 내용

    // 4. 작가

    // 5. 출판사

    // 6. 제작자
}
