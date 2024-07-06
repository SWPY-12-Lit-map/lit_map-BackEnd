package com.lit_map_BackEnd.domain.character.controller;

import com.lit_map_BackEnd.common.exception.code.SuccessCode;
import com.lit_map_BackEnd.common.exception.response.SuccessResponse;
import com.lit_map_BackEnd.domain.character.dto.CastRequestDto;
import com.lit_map_BackEnd.domain.character.service.CastService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cast")
@RequiredArgsConstructor
@Validated
public class CastController {

    private final CastService castService;

    @PostMapping("")
    @Operation(summary = "해당 작품에 캐릭터 등록", description = "작품에 등록될 캐릭터 등록하기")
    public ResponseEntity<SuccessResponse> insertCharacter(@RequestBody List<@Valid CastRequestDto> castRequestDto) {
        //int N = castService.insertCharacter(castRequestDto);

        SuccessResponse res = SuccessResponse.builder()
                //.result(N)
                .resultCode(SuccessCode.INSERT_SUCCESS.getStatus())
                .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
