package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.MemberDto;
import com.lit_map_BackEnd.domain.member.dto.PublisherDto;
import com.lit_map_BackEnd.domain.member.entity.Publisher;

public interface PublisherService {
    void requestPublisherWithdrawal(Long memberId);
    void approvePublisherWithdrawal(Long memberId);;
}
