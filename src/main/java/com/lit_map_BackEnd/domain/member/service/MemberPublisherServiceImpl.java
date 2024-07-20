package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.category.repository.CategoryRepository;
import com.lit_map_BackEnd.domain.member.dto.*;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.Publisher;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.member.repository.PublisherRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final EmailService emailService;
    private final HttpSession session;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate = new RestTemplate();

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
    } // 비밀번호 유효성 메서드 -> 영어+숫자 섞어서 사용

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
                .memberRoleStatus(memberDto.getMemberRoleStatus() != null ? memberDto.getMemberRoleStatus() : MemberRoleStatus.PENDING_MEMBER) // 기본값 설정
                //.role(Role.PENDING_MEMBER) // 기본값 설정
                .build();

        if (member.getWithdrawalRequested() == null) {
            member.setWithdrawalRequested(false);  // 기본값 설정
        }

        return memberRepository.save(member);
    } // 1인작가 회원가입

    @Override
    @Transactional
    public Publisher savePublisher(PublisherDto publisherDto) {
//        if (publisherRepository.findByPublisherNumber(publisherDto.getPublisherNumber()).isPresent()) {
//            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_PUBLISHER);
//        }

        if (memberRepository.findByLitmapEmail(publisherDto.getLitmapEmail()).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_EMAIL);
        }

        Publisher publisher = Publisher.builder()
                .publisherNumber(publisherDto.getPublisherNumber())
                .publisherName(publisherDto.getPublisherName())
                .publisherAddress(publisherDto.getPublisherAddress())
                .publisherPhoneNumber(publisherDto.getPublisherPhoneNumber())
                .publisherCeo(publisherDto.getPublisherCeo())
                .build();

        Member member = Member.builder()
                .litmapEmail(publisherDto.getLitmapEmail())
                .name(publisherDto.getName())
                .password(passwordEncoder.encode(publisherDto.getPassword()))
                .nickname(publisherDto.getNickname())
                .myMessage(publisherDto.getMyMessage())
                .userImage(publisherDto.getUserImage())
                .publisher(publisher)
                //.role(Role.PUBLISHER_MEMBER) // 출판사 회원 역할 설정
                .build();

        publisher.getMemberList().add(member);

        return publisherRepository.save(publisher);
    } // 출판사 회원가입

    @Override
    public Member login(String litmapEmail, String password) {
        Optional<Member> memberOptional = memberRepository.findByLitmapEmail(litmapEmail);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (passwordEncoder.matches(password, member.getPassword())) {
                session.setAttribute("member", member);
                return member;
            } else {
                throw new BusinessExceptionHandler(ErrorCode.PASSWORDS_DO_NOT_MATCH);
            }
        } else {
            throw new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND);
        }
    } // 로그인

    @Override
    public void logout() {
        session.invalidate();
    } // 로그아웃

    @Override
    public String findPw(MailDto request) throws Exception {
        // 요청 검증
        Member member = memberRepository.findByLitmapEmail(request.getEmail())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // 임시 비밀번호 생성
        String tempPw = generateTempPassword();

        // 이메일 전송을 위한 내용 설정
        String subject = "litmap 임시 비밀번호 발송";
        String content = "안녕하세요,\n\n임시 비밀번호는 다음과 같습니다: " + tempPw + "\n로그인 후 비밀번호를 변경해 주세요.\n\n감사합니다.";

        // 이메일 전송
        emailService.sendEmail(request.getEmail(), subject, content);

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
    }// 공공 API로 출판사 정보 가져오기 + 사업자확인도 필요

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

    public Member updateMember(String litmapEmail, MemberUpdateDto memberUpdateDto) {
        Member member = memberRepository.findByLitmapEmail(litmapEmail)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        updateMemberFields(member, memberUpdateDto);

        return memberRepository.save(member);
    } // 1인 작가 마이페이지 수정

    public Member updatePublisherMember(String litmapEmail, PublisherUpdateDto publisherUpdateDto) {
        Member member = memberRepository.findByLitmapEmail(litmapEmail)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Publisher publisher = member.getPublisher();
        if (publisher == null) {
            throw new BusinessExceptionHandler(ErrorCode.PUBLISHER_NOT_FOUND);
        }

        updatePublisherFields(publisher, publisherUpdateDto);
        updateMemberFields(member, publisherUpdateDto);

        return memberRepository.save(member);
    } // 출판사 직원 마이페이지 수정

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
    }

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
    }

}
