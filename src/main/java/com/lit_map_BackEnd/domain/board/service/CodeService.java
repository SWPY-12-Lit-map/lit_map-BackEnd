package com.lit_map_BackEnd.domain.board.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.board.dto.CodeDto;
import com.lit_map_BackEnd.domain.board.entity.Example;
import com.lit_map_BackEnd.domain.board.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CodeService {

    private final ExampleRepository exampleRepository;

    @Transactional
    public int insertCode(CodeDto codeDto) {
        Example build = Example.builder()
                .name(codeDto.getName())
                .age(codeDto.getAge())
                .build();
        try {
            exampleRepository.save(build);
            return 1;
        } catch (Exception e) {
            throw new BusinessExceptionHandler(ErrorCode.INSERT_ERROR);
        }
    }
}
