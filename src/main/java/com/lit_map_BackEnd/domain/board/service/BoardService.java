package com.lit_map_BackEnd.domain.board.service;

import com.lit_map_BackEnd.domain.board.dto.ConfirmListDto;
import com.lit_map_BackEnd.domain.work.dto.WorkResponseDto;
import com.lit_map_BackEnd.domain.work.entity.Work;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface BoardService {
    List<ConfirmListDto> getConfirmData();
    List<WorkResponseDto> getMyWorkList();
    Slice<WorkResponseDto> getWorkListByView(int pageNum);
    Slice<WorkResponseDto> getWorkListByUpdateDate(int pageNum);

    List<WorkResponseDto> getWorkByCategoryAndGenre(Long categoryId, Long genreId);

}
