package com.lit_map_BackEnd.domain.board.service;

import com.lit_map_BackEnd.domain.board.dto.*;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public interface BoardService {
    List<ConfirmListDto> getConfirmData();
    MyWorkListResponseDto getMyWorkList();
    Slice<WorkResponseDto> getWorkListByView(int pageNum);
    Slice<WorkResponseDto> getWorkListByUpdateDate(int pageNum);
    List<Map<String, Object>> getWorkByCategoryAndGenre(Long categoryId, Long genreId);
    Map<String, CategoryResultDto> findWorksBySearch(SearchDto searchDto);
    Map<String, Long> getWorksCount();
}
