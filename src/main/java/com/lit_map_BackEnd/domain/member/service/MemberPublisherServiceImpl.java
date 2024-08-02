package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.util.SessionUtil;
import com.lit_map_BackEnd.domain.admin.service.AdminAuthService;
import com.lit_map_BackEnd.domain.mail.dto.MailDto;
import com.lit_map_BackEnd.domain.mail.service.MailService;
import com.lit_map_BackEnd.domain.member.dto.*;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.Publisher;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.member.repository.PublisherRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberPublisherServiceImpl implements MemberPublisherService {

    private final MemberRepository memberRepository;
    private final PublisherRepository publisherRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final HttpSession session;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AdminAuthService adminAuthService;

    @Value("${external.api.publisher.url}")
    private String publisherApiUrl;

    private void validateMemberDto(MemberDto memberDto) {
        if (memberRepository.findByLitmapEmail(memberDto.getLitmapEmail()).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_EMAIL);
        } // 릿맵 이메일 중복확인

        if (memberRepository.findByWorkEmail(memberDto.getWorkEmail()).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_WORK_EMAIL);
        } // 1인작가 업무용 이메일 중복확인

        if (memberDto.getLitmapEmail().equals(memberDto.getWorkEmail())) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_EMAILS);
        } // 1인작가 릿맵, 업무용 동일성 확인 -> 같으면 가입 불가

        if (!memberDto.getPassword().equals(memberDto.getConfirmPassword())) {
            throw new BusinessExceptionHandler(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        } // 비밀번호 일치 확인

        if (!isValidPassword(memberDto.getPassword())) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_PASSWORD_FORMAT);
        } // 비밀번호 유효성 검사

        if (containsProhibitedWords(memberDto.getNickname())) {
            throw new BusinessExceptionHandler(ErrorCode.PROHIBITED_NICKNAME);
        } // 닉네임 유효성 검사 -> 금지어 사용시 가입 불가

        if (memberRepository.findByNickname(memberDto.getNickname()).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.PROHIBITED_NICKNAME);
        } // 닉네임 중복 확인

    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$";
        return Pattern.compile(regex).matcher(password).matches();
    } // 비밀번호 유효성 메서드 -> 영어+숫자 섞어서 8자 ~ 20자 사용

    private boolean containsProhibitedWords(String nickname) {
        String[] prohibitedWords = {"admin", "관리자", "system"};

        for (String word : prohibitedWords) {
            if (nickname.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    } // 닉네임 금지어 검사 메소드 -> 금지어는 계속 추가 예정

    @Override
    @Transactional
    public Member saveMember(MemberDto memberDto) {
        validateMemberDto(memberDto);

        Member member = Member.builder()
                .litmapEmail(memberDto.getLitmapEmail())
                .workEmail(memberDto.getWorkEmail())
                .name(memberDto.getName())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .nickname(memberDto.getNickname())
                .myMessage(memberDto.getMyMessage())
                .userImage(memberDto.getUserImage())
                .urlLink(memberDto.getUrlLink())
                .memberRoleStatus(MemberRoleStatus.PENDING_MEMBER) // 기본값 설정
                .build();

        Member savedMember = memberRepository.save(member);

        return savedMember;


    } // 1인작가 회원가입

    //   mailService.sendEmail(member.getLitmapEmail(), subject, content);
    //  return member;
    //} // 1인작가 회원승인

    public boolean checkLitmapEmailExists(String litmapEmail) {
        return memberRepository.findByLitmapEmail(litmapEmail).isPresent();
    } // 회원가입시 사용하는 이메일 중복 여부 확인 메서드
//    @Override
//    public boolean checkWorkEmailExists(String workEmail) {
//        return memberRepository.findByWorkEmail(workEmail).isPresent();
//    } // 회원가입시 사용하는 이메일 중복 여부 확인 메서드

    // PublisherDto를 Publisher 엔티티로 변환하는 메서드
    private Publisher convertToPublisher(PublisherDto publisherDto) {
        return Publisher.builder()
                .publisherNumber(publisherDto.getPublisherNumber())
                .publisherName(publisherDto.getPublisherName())
                .publisherAddress(publisherDto.getPublisherAddress())
                .publisherPhoneNumber(publisherDto.getPublisherPhoneNumber())
                .publisherCeo(publisherDto.getPublisherCeo())
                .memberList(new ArrayList<>()) // 초기화된 memberList 추가
                .build();
    }
    // PublisherDto와 Publisher 엔티티를 사용하여 Member 엔티티를 생성하는 메서드
    private Member convertToMember(PublisherDto publisherDto, Publisher publisher) {
        return Member.builder()
                .litmapEmail(publisherDto.getLitmapEmail())
                .name(publisherDto.getName())
                .password(passwordEncoder.encode(publisherDto.getPassword()))
                .nickname(publisherDto.getNickname())
                .myMessage(publisherDto.getMyMessage())
                .userImage(publisherDto.getUserImage())
                .publisher(publisher)
                .memberRoleStatus(MemberRoleStatus.PUBLISHER_MEMBER)
                .build();
    }

    @Override
    @Transactional
    public PublisherDto savePublisher(PublisherDto publisherDto) {

        if (memberRepository.findByLitmapEmail(publisherDto.getLitmapEmail()).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_EMAIL);
        }
        Publisher publisher = convertToPublisher(publisherDto); // PublisherDto를 Publisher 엔티티로 변환
        Member member = convertToMember(publisherDto, publisher); // PublisherDto와 변환된 Publisher 엔티티를 사용하여 Member 엔티티를 생성

        member.setMemberRoleStatus(MemberRoleStatus.PUBLISHER_MEMBER); // 출판사 직원 상태로 설정
        publisher.getMemberList().add(member); // publisher의 memberList에 생성된 member 추가

        Publisher savedPublisher = publisherRepository.save(publisher); // Publisher 엔티티를 데이터베이스에 저장

        // (출판사) 회원 가입 승인 이메일 전송
        String subject = "[litmap] 회원 가입 승인";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,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\"/>"
                + "<br><h2>회원 가입 완료</h2><h4>"
                + publisherDto.getPublisherName()
                + "님 회원 가입 승인되었습니다.</h4></div>";

        mailService.sendEmail(publisherDto.getLitmapEmail(), subject, content);

        // 출판사 직원 로그인 상태 유지
        loginAfterRegistration(member);

        return convertToPublisherDto(savedPublisher); // 저장된 Publisher 엔티티를 PublisherDto로 변환하여 반환
    } // 출판사 회원가입

    @Override
    public Member findByLitmapEmail(String litmapEmail) {
        return memberRepository.findByLitmapEmail(litmapEmail)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public PublisherDto getProfile(HttpServletRequest request) {
        Member member = SessionUtil.getLoggedInUser(request);
        if (member == null || member.getPublisher() == null) {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
        return convertToPublisherDto(member.getPublisher());
    } // 프로필 조회

    @Override
    public PublisherDto getPublisherProfile(Long publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PUBLISHER_NOT_FOUND));

        return convertToPublisherDto(publisher);
    } // 출판사 프로필 조회

    private void loginAfterRegistration(Member member) { // 회원가입 후 로그인 상태 유지를 위한 메서드
        SessionUtil.setLoggedInUser(request, member);
    }

    @Override
    @Transactional
    public Member login(String litmapEmail, String password) {
        Optional<Member> memberOptional = memberRepository.findByLitmapEmail(litmapEmail);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (passwordEncoder.matches(password, member.getPassword())) {
                SessionUtil.setLoggedInUser(request, member); // 세션에 사용자 정보 저장
                if (member.getPublisher() != null) {
                    // 출판사 직원인 경우 PublisherDto를 세션에 저장
                    PublisherDto publisherDto = convertToPublisherDto(member.getPublisher());
                    request.getSession().setAttribute("publisherDto", publisherDto);
                }
                return member;
            } else {
                throw new BusinessExceptionHandler(ErrorCode.PASSWORDS_DO_NOT_MATCH);
            }
        } else {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Override
    public PublisherDto loginPublisher(String litmapEmail, String password) {
        return null;
    }

    @Override
    public void logout() {
        request.getSession().invalidate();
    } // 로그아웃

    @Override
    public String findPw(MailDto request) throws Exception {
        // 요청 검증
        Member member = memberRepository.findByLitmapEmail(request.getEmail())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // 임시 비밀번호 생성
        String tempPw = generateTempPassword();

        // 이메일 전송을 위한 내용 설정
        String subject = "[litmap] 임시 비밀번호 발송";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAI4AAAAyCAYAAACK9eMGAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAcZSURBVHgB7Z19jBxlHcc/M7vblu5Ra9HaKrZ7d3tt4xulQKSmkWrUNCJRE4n9A08TXkQbUNu73tFKWLRy7d0VgQRpSYhRE1FqpMTEgGJtQk3QVEEEgr1tOaGGopUUOa7c3e48fJ+7Fu6uu9d7mX2bmU9ynXnmefbZ2Xm+8/s973WIKEgG3HNYviiHtzAG8wzGtdfn8erBDfy3j5AT7yT9Ij5jYHUb2aPUEBnWxmdzdE0CPq3gWv2GCyCfnDUu3eskFzNOOF2kf6n0H8N/jnt4N7i491MidN8/O8iFN+9hT34qn4s7cD4+Y6gdMqTm1+HeaHjxenAWT+/ezTwHx/fnKObYf0pRRsrz5RzOxnZ6fg5ZpkqckJKRK0rStMnBbJFY5hMqzKMD5Jq38q+XmCahFM52GpbEcHfrAa6rJevoB7I0t7VweCszJHTC6ab+4wZ3j04XEiIkmON5zLVtHN6LD7iEiJ2kr/KI/Z6QiUayeUwueZVforGExuKo9fgZD37ihOtleU0/9qZN9NyNz4RCOB2kUjqo9RAe0egFeQZyX9pE73OUgMA/yIx+Y4L4H/QgzyMkqLPyTpc3LmkpkWgsgbc4dTRuUMupgVBgTsrWbNnM4TsoMYEWjnVRBucuQoAs6uMuzjUbyT5DGQi0q0oQu4XAY4x6rbf9mQvXlEs0lsBaHNvJJ2vTTLDReKDzlRZ69kMP5SSwFkcDg5cTbIv60BCsaiW7nwoQWIsj872+toZbJ4fqMq9oGHtzG9n7qCCBFI6dIiEr/lEChkRzJIf3hXaO/IMKE0hTfg69F+swm2Bxn0SzqhpEYwmkxXGYtRQ8gkCMxP9h8JpWnq+oaxpPQIXjpQgIrfzzKR2eosoIpKvyMEkiSkoghaMe1FlElJRQzceJ8I9IOBHTIhJOxLSoaeFkSM3pJLWIiLJTs8K5jRXnJYnvc0h8iIiyU5P9ONtofH+C3O90uoKIilBzwrGuycH9jQYwI9FUkJoSzk4alhliDxtMPREVpWbqOB00XuThPhaJpjqoCeF0kb4ijvNHQreQrnqpeuF003idDnYF4rlEVA1VXcfZQUObwdlOxDC3k75sCN49UZpZ5B79Dr0ndpA+X1ahw8P5Uxs9u0bl8cGBInnMIffkILPnOuSb7aSxVrIPdJJepbG/GwzeI60c/sXptFUrHLmnNmzVJuIt8ph2FeK6idIMEl/N8FIZMx+cqxy8hMK7RuXRHS+SxyDuJTCEWq0ddisUXXrAwVmgeuXXDLyqcHULR+6p09ipKBFjyOPcISE8VCjOJfZtFfZyJoksUZfS9498Fjux/2KmQFUJ5y7Sswcw94ZgWcu0aCf7SLG4Lho/LwszaeHkie3YwnP/s+edNKpvzBkjHIUb5O4yklj98GzncVSNcH7Iyvlv0Pdr3fAniCiICvgePZ9LC8WpaFN+rumwy6ZliW4pJBpLVbSqfsDSxXn6DugWI9FMgBWHDitVrDkdT4z+U0E/qeP+GO4JfMHsy5GrV75XF4qtuMWx67vtbhLh2Rhg5hjyV2ym9xgzQK2v73XR1D8S8lYXSOLdRG+v0rxQaH1aRYXTwZIGiWafbmspEZNG9ZN1qn9MtNfy42dbjKjYb76dxmGqVEw421l2QQxP405E82mmiJrTP54oXk3x9d7wxkpnkiP/jRiJgrusnuTkoTqStq70tJrkw/tUx8ifyOMq7Lw09h4qgMzfp2QJf6XTdxAxaTzMzbINd54t3Wv0H0wyt+ALad2PPaqivU79Myk17/eOc3vP6u/DpwMDeMdVEb47JjGNzqfswtENqzPJ7Hasm42YEm0cOVjoeobUojoSHerdPa7e3eH+LzXPXVmdW+WGni70GT3/r8qqrJd1OZph7cNFvpIE/25Sed0j0VrBHjh9vazC2Uljy0jHU4Sf1OGmbe+unNQr6gv77o1kBySg/zC8k93E2LlNSYr/7wnFakplE47c0zapdsYbM0eMRS/jl/Uydo+EzAKNQ/1WLdWrT7uksyFh7NUwxrHi8XZV7JlDFGURjgbK7NhHOxG+cKqH/bMq0G/JHV1md+WSAOT+XXv+yTjx5zXW91MX9/ZNHPr7xLmZ3S1ki7qqkfqoKa9wruTK2KU8If/ItUTMmAwfqKtjcKusytclmnfqUp/qKj9SK2rXxlO7WKhu80XFXa/TZg+vWQJ6QqLq2Ex2T6E8Ja5bu2naUOw75SUWFGqsl0w4O3jXuS5/u19m9HIifCHDs31qXHxETeMXJIbOfnK7MvSO6SlW3eZBHR7cyYplhlyr0n1O9Z+5Z+bmarTbO6a4JQosKfadp0SjdIzpN3K207AGn2nnyAEpf6HBfS8lpp+h3vEP7/ssr08w9D5KgP1t46910rTSGZ7G4C8ebn8bh/4y+loX70m28vLrk83jOi5K3Mtfh/CZNwEgcQOnKFrfYwAAAABJRU5ErkJggg==\"/>"
                + "        <br><br>"
                + "        <h2>비밀번호 확인</h2>"
                + "        <h4>고객님의 임시 비밀번호를 아래에서 확인하세요.</h4>"
                + "        <br><br><br>"
                + "        <h4>" + tempPw + "</h4>";


        // 이메일 전송
        mailService.sendEmail(member.getLitmapEmail(), subject, content);  // 수신자를 litmapEmail로 설정

        // 회원의 비밀번호를 임시 비밀번호로 설정
        member.setPassword(passwordEncoder.encode(tempPw));
        memberRepository.save(member);

        return "임시 비밀번호가 발급되었습니다.";
    }

    private String generateTempPassword() {
        char[] charSet = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
                '4', '5', '6', '7', '8', '9'};
        StringBuilder tempPw = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int idx = random.nextInt(charSet.length);
            tempPw.append(charSet[idx]);
        }
        return tempPw.toString();
    }

    @Override
    public Publisher fetchPublisherFromApi(Long publisherNumber) {
        String url = UriComponentsBuilder.fromHttpUrl(publisherApiUrl)
                .queryParam("publisherNumber", publisherNumber)
                .toUriString();
        PublisherDto publisherDto = restTemplate.getForObject(url, PublisherDto.class);
        if (publisherDto != null) {
            Publisher publisher = Publisher.builder()
                    .publisherNumber(publisherDto.getPublisherNumber())
                    .publisherName(publisherDto.getPublisherName())
                    .publisherAddress(publisherDto.getPublisherAddress())
                    .publisherPhoneNumber(publisherDto.getPublisherPhoneNumber())
                    .publisherCeo(publisherDto.getPublisherCeo())
                    .build();
            return publisher;
        }
        throw new BusinessExceptionHandler(ErrorCode.PUBLISHER_NOT_FOUND);
    }// 공공 API를 통해 출판사 정보 가져오기 메서드 -> 있지만 아직 사용안함

    @Override
    public Member verifyPassword(String litmapEmail, String password) {
        Optional<Member> memberOptional = memberRepository.findByLitmapEmail(litmapEmail);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (passwordEncoder.matches(password, member.getPassword())) {
                return member;
            } else {
                throw new BusinessExceptionHandler(ErrorCode.PASSWORDS_DO_NOT_MATCH);
            }
        } else {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
    } // 마이페이지 진입시 비밀번호 입력

    @Override
    public String findMemberEmail(String workEmail, String name) {
        Optional<Member> memberOptional = memberRepository.findByWorkEmail(workEmail);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (member.getName().equals(name)) {
                return member.getLitmapEmail();
            } else {
                throw new BusinessExceptionHandler(ErrorCode.INVALID_USER_INFO);
            }
        } else {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
    } // 1인작가 이메일 찾기 : 업무용이메일, 이름 사용

    @Override
    public String findPublisherEmail(Long publisherNumber, String publisherName, String memberName) {
        List<Publisher> publishers = publisherRepository.findByPublisherNumber(publisherNumber);
        if (!publishers.isEmpty()) {
            for (Publisher publisher : publishers) {
                if (publisher.getPublisherName().equals(publisherName)) {
                    for (Member member : publisher.getMemberList()) {
                        if (member.getName().equals(memberName)) {
                            return member.getLitmapEmail();
                        }
                    }
                }
            }
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        } else {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
    } // 출판사 이메일 찾기 : 사업자번호, 출판사이름, 회원이름 사용 / 모든 결과를 검사하여 필요한 경우 여러 결과 중 하나를 반환

    @Override
    @Transactional
    public Member updateMember(String litmapEmail, MemberUpdateDto memberUpdateDto) {
        Member member = memberRepository.findByLitmapEmail(litmapEmail)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        updateMemberFields(member, memberUpdateDto);
        Member updatedMember = memberRepository.save(member);

        // 세션 정보 업데이트
        SessionUtil.setLoggedInUser(request, updatedMember);

        return updatedMember;
    } // 1인 작가 마이페이지 (회원정보) 수정

    @Override
    @Transactional
    public PublisherDto updatePublisherMember(String litmapEmail, PublisherUpdateDto publisherUpdateDto) {
        Member member = memberRepository.findByLitmapEmail(litmapEmail)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Publisher publisher = member.getPublisher();
        if (publisher == null) {
            throw new BusinessExceptionHandler(ErrorCode.PUBLISHER_NOT_FOUND);
        }

        updatePublisherFields(publisher, publisherUpdateDto);
        updateMemberFields(member, publisherUpdateDto);
        memberRepository.save(member); // 출판사 직원 정보를 업데이트할 때, memberRepository에 저장

        PublisherDto updatedPublisherDto = convertToPublisherDto(publisher);

        // 세션 정보 업데이트
        SessionUtil.setLoggedInUser(request, member); // 세션에 수정된 출판사 직원 정보 저장
        request.getSession().setAttribute("publisherDto", updatedPublisherDto); // 세션에 수정된 출판사 정보 저장

        return updatedPublisherDto;
    } // 출판사 직원 마이페이지 (회원정보) 수정

    private void updateMemberFields(Member member, MemberUpdateDto memberUpdateDto) {
        if (memberUpdateDto.getWorkEmail() != null) {
            member.setWorkEmail(memberUpdateDto.getWorkEmail());
        }
        if (memberUpdateDto.getName() != null) {
            member.setName(memberUpdateDto.getName());
        }
        if (memberUpdateDto.getPassword() != null && memberUpdateDto.getConfirmPassword() != null && memberUpdateDto.getPassword().equals(memberUpdateDto.getConfirmPassword())) {
            member.setPassword(passwordEncoder.encode(memberUpdateDto.getPassword()));
        }
        if (memberUpdateDto.getNickname() != null) {
            member.setNickname(memberUpdateDto.getNickname());
        }
        if (memberUpdateDto.getUserImage() != null) {
            member.setUserImage(memberUpdateDto.getUserImage());
        }
        if (memberUpdateDto.getMyMessage() != null) {
            member.setMyMessage(memberUpdateDto.getMyMessage());
        }
        if (memberUpdateDto.getUrlLink() != null) {
            member.setUrlLink(memberUpdateDto.getUrlLink());
        }

        // MemberRoleStatus 유지
        member.setMemberRoleStatus(member.getMemberRoleStatus());
    } // 1인작가 정보 업데이트

    private void updatePublisherFields(Publisher publisher, PublisherUpdateDto publisherUpdateDto) {
        if (publisherUpdateDto.getPublisherAddress() != null) {
            publisher.setPublisherAddress(publisherUpdateDto.getPublisherAddress());
        }
        if (publisherUpdateDto.getPublisherPhoneNumber() != null) {
            publisher.setPublisherPhoneNumber(publisherUpdateDto.getPublisherPhoneNumber());
        }
        if (publisherUpdateDto.getPublisherCeo() != null) {
            publisher.setPublisherCeo(publisherUpdateDto.getPublisherCeo());
        }
    } // 출판사 정보 업데이트


    private PublisherDto convertToPublisherDto(Publisher publisher) {
        // 필요한 필드를 PublisherDto로 변환
        return PublisherDto.builder()
                .publisherNumber(publisher.getPublisherNumber())
                .publisherName(publisher.getPublisherName())
                .publisherAddress(publisher.getPublisherAddress())
                .publisherPhoneNumber(publisher.getPublisherPhoneNumber())
                .publisherCeo(publisher.getPublisherCeo())
                .litmapEmail(publisher.getMemberList().get(0).getLitmapEmail())
                .name(publisher.getMemberList().get(0).getName())
                .password(publisher.getMemberList().get(0).getPassword())
                .nickname(publisher.getMemberList().get(0).getNickname())
                .myMessage(publisher.getMemberList().get(0).getMyMessage())
                .userImage(publisher.getMemberList().get(0).getUserImage())
                .memberRoleStatus(publisher.getMemberList().get(0).getMemberRoleStatus()) // 추가: 회원 상태 반환
                .build();
    } // Publisher 엔티티를 PublisherDto로 변환하는 메서드
}