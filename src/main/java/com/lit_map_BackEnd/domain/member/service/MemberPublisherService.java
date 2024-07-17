package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.MailDto;
import com.lit_map_BackEnd.domain.member.dto.MemberDto;
import com.lit_map_BackEnd.domain.member.dto.PublisherDto;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.Publisher;

public interface MemberPublisherService {
    // 1인작가, 출판사 : 회원가입, 로그인, 비밀번호찾기

    Member saveMember(MemberDto memberDto); // 작가 회원가입
    Publisher savePublisher(PublisherDto publisherDto); // 출판사 회원가입

    Member login(String litmapEmail, String password); // 로그인
    void logout(); // 로그아웃

    String findPw(MailDto request) throws Exception; // 비밀번호 찾기 - 릿맵이메일 공통

    Publisher fetchPublisherFromApi(Long publisherNumber); // 공공 API로 출판사 정보 가져오기 + 사업자확인도 필요

    String findMemberEmail(String workEmail, String name); // 1인작가 이메일 찾기
    String findPublisherEmail(Long publisherNumber, String publisherName, String name); // 출판사 이메일 찾기
    // 출판사에서 ceo가 아닌 그냥 이름

}
