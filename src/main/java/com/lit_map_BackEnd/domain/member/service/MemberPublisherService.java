package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.domain.member.dto.*;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.Publisher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

public interface MemberPublisherService {
    // 1인작가, 출판사 : 회원가입, 로그인, 비밀번호찾기

    Member saveMember(MemberDto memberDto); // 작가 회원가입
    Member approveMember(Long memberId); // 회원 승인

    boolean checkLitmapEmailExists(String litmapEmail);     // 이메일 중복 여부 확인 메서드
    PublisherDto savePublisher(PublisherDto publisherDto); // 출판사 회원가입

    Member findByLitmapEmail(String litmapEmail);

    PublisherDto getProfile(HttpServletRequest request); // 수정된 부분

    PublisherDto getPublisherProfile(Long publisherId); // 출판사 프로필 조회

    @Transactional
    Member login(String litmapEmail, String password); // 로그인
    PublisherDto loginPublisher(String litmapEmail, String password); // 출판사 직원 로그인

    void logout(); // 로그아웃

    String findPw(MailDto request) throws Exception; // 비밀번호 찾기 - 릿맵이메일 공통

    Publisher fetchPublisherFromApi(Long publisherNumber); // 공공 API로 출판사 정보 가져오기 + 사업자확인도 필요

    boolean verifyPassword(String litmapEmail, String password);

    String findMemberEmail(String workEmail, String name); // 1인작가 이메일 찾기
    String findPublisherEmail(Long publisherNumber, String publisherName, String name); // 출판사 이메일 찾기

    Member updateMember(String litmapEmail, MemberUpdateDto memberUpdateDto); // 1인작가 정보 수정
    PublisherDto updatePublisherMember(String litmapEmail, PublisherUpdateDto publisherUpdateDto); // 출판사 직원 정보 수정

    // 새로운 메서드 정의
    //Member getLatestMemberInfo(String litmapEmail);
}