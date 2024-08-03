package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.common.util.SessionUtil;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${external.api.publisher.url}")
    private String publisherApiUrl;

    private void validateMemberDto(MemberDto memberDto) {
        if (memberRepository.findByLitmapEmail(memberDto.getLitmapEmail()).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_EMAIL);
        }

        if (memberRepository.findByWorkEmail(memberDto.getWorkEmail()).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_WORK_EMAIL);
        }

        if (memberDto.getLitmapEmail().equals(memberDto.getWorkEmail())) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_EMAILS);
        }

        if (!memberDto.getPassword().equals(memberDto.getConfirmPassword())) {
            throw new BusinessExceptionHandler(ErrorCode.PASSWORDS_DO_NOT_MATCH);
        }

        if (!isValidPassword(memberDto.getPassword())) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_PASSWORD_FORMAT);
        }

        if (containsProhibitedWords(memberDto.getNickname())) {
            throw new BusinessExceptionHandler(ErrorCode.PROHIBITED_NICKNAME);
        }

        if (memberRepository.findByNickname(memberDto.getNickname()).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.PROHIBITED_NICKNAME);
        }
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,20}$";
        return Pattern.compile(regex).matcher(password).matches();
    }

    private boolean containsProhibitedWords(String nickname) {
        String[] prohibitedWords = {"admin", "관리자", "system"};

        for (String word : prohibitedWords) {
            if (nickname.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

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
                .memberRoleStatus(MemberRoleStatus.PENDING_MEMBER)
                .build();

        Member savedMember = memberRepository.save(member);
        return savedMember;
    }

    public boolean checkLitmapEmailExists(String litmapEmail) {
        return memberRepository.findByLitmapEmail(litmapEmail).isPresent();
    }

    @Override
    @Transactional
    public PublisherDto savePublisher(PublisherDto publisherDto) {
        if (memberRepository.findByLitmapEmail(publisherDto.getLitmapEmail()).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_EMAIL);
        }

        Publisher publisher = Publisher.builder()
                .publisherNumber(publisherDto.getPublisherNumber())
                .publisherName(publisherDto.getPublisherName())
                .publisherAddress(publisherDto.getPublisherAddress())
                .publisherPhoneNumber(publisherDto.getPublisherPhoneNumber())
                .publisherCeo(publisherDto.getPublisherCeo())
                .memberList(new ArrayList<>())
                .build();

        Member member = Member.builder()
                .litmapEmail(publisherDto.getLitmapEmail())
                .name(publisherDto.getName())
                .password(passwordEncoder.encode(publisherDto.getPassword()))
                .nickname(publisherDto.getNickname())
                .myMessage(publisherDto.getMyMessage())
                .userImage(publisherDto.getUserImage())
                .publisher(publisher)
                .memberRoleStatus(MemberRoleStatus.PUBLISHER_MEMBER)
                .build();

        member.setMemberRoleStatus(MemberRoleStatus.PUBLISHER_MEMBER);
        publisher.getMemberList().add(member);

        Publisher savedPublisher = publisherRepository.save(publisher);

        String subject = "[litmap] 회원 가입 승인";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,...\"/>"
                + "<br><h2>회원 가입 완료</h2><h4>"
                + publisherDto.getPublisherName()
                + "님 회원 가입 승인되었습니다.</h4></div>";

        mailService.sendEmail(publisherDto.getLitmapEmail(), subject, content);

        loginAfterRegistration(member);

        return convertToPublisherDto(savedPublisher);
    }

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
    }

    @Override
    public PublisherDto getPublisherProfile(Long publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.PUBLISHER_NOT_FOUND));

        return convertToPublisherDto(publisher);
    }

    private void loginAfterRegistration(Member member) {
        SessionUtil.setLoggedInUser(request, member);
    }

    @Override
    @Transactional
    public Member login(String litmapEmail, String password) {
        Optional<Member> memberOptional = memberRepository.findByLitmapEmail(litmapEmail);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (passwordEncoder.matches(password, member.getPassword())) {
                SessionUtil.setLoggedInUser(request, member);
                if (member.getPublisher() != null) {
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
    }

    @Override
    public String findPw(MailDto request) throws Exception {
        Member member = memberRepository.findByLitmapEmail(request.getEmail())
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        String tempPw = generateTempPassword();

        String subject = "[litmap] 임시 비밀번호 발송";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,...\"/>"
                + "<br><br><h2>비밀번호 확인</h2><h4>고객님의 임시 비밀번호를 아래에서 확인하세요.</h4>"
                + "<br><br><br><h4>" + tempPw + "</h4>";

        mailService.sendEmail(member.getLitmapEmail(), subject, content);

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
    }

    @Override
    public boolean verifyPassword(String sessionPassword, String password) {
        if (passwordEncoder.matches(password, sessionPassword)) {
            return true;
        } else throw new BusinessExceptionHandler(ErrorCode.PASSWORDS_DO_NOT_MATCH);
    }

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
    }

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
    }

    @Override
    @Transactional
    public Member updateMember(String litmapEmail, MemberUpdateDto memberUpdateDto) {
        Member member = memberRepository.findByLitmapEmail(litmapEmail)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        updateMemberFields(member, memberUpdateDto);
        Member updatedMember = memberRepository.save(member);

        SessionUtil.setLoggedInUser(request, updatedMember);

        return updatedMember;
    }

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
        updatePublisherMemberFields(member, publisherUpdateDto);
        memberRepository.save(member);

        PublisherDto updatedPublisherDto = convertToPublisherDto(publisher);

        SessionUtil.setLoggedInUser(request, member);
        request.getSession().setAttribute("publisherDto", updatedPublisherDto);

        return updatedPublisherDto;
    }

    private void updatePublisherMemberFields(Member member, PublisherUpdateDto publisherUpdateDto) {
        if (publisherUpdateDto.getName() != null) {
            member.setName(publisherUpdateDto.getName());
        }
        if (publisherUpdateDto.getPassword() != null && !publisherUpdateDto.getPassword().isEmpty()) {
            if (!publisherUpdateDto.getPassword().equals(publisherUpdateDto.getConfirmPassword())) {
                throw new BusinessExceptionHandler(ErrorCode.PASSWORDS_DO_NOT_MATCH);
            }
            member.setPassword(passwordEncoder.encode(publisherUpdateDto.getPassword()));
        }
        if (publisherUpdateDto.getMemberRoleStatus() != null) {
            member.setMemberRoleStatus(publisherUpdateDto.getMemberRoleStatus());
        }
    }

    @Override
    @Transactional
    public Member updateProfile(String litmapEmail, ProfileUpdateDto profileUpdateDto) {
        Member member = memberRepository.findByLitmapEmail(litmapEmail)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        if (profileUpdateDto.getNickname() != null) {
            member.setNickname(profileUpdateDto.getNickname());
        }
        if (profileUpdateDto.getUserImage() != null) {
            member.setUserImage(profileUpdateDto.getUserImage());
        }
        if (profileUpdateDto.getMyMessage() != null) {
            member.setMyMessage(profileUpdateDto.getMyMessage());
        }
        Member updatedMember = memberRepository.save(member);

        // 최신 정보를 세션에 업데이트
        SessionUtil.setLoggedInUser(request, updatedMember);

        return updatedMember;
    }

    private void updateMemberFields(Member member, MemberUpdateDto memberUpdateDto) {
        if (memberUpdateDto.getWorkEmail() != null) {
            member.setWorkEmail(memberUpdateDto.getWorkEmail());
        }
        if (memberUpdateDto.getName() != null) {
            member.setName(memberUpdateDto.getName());
        }
        if (memberUpdateDto.getPassword() != null && !memberUpdateDto.getPassword().isEmpty()) {
            if (!memberUpdateDto.getPassword().equals(memberUpdateDto.getConfirmPassword())) {
                throw new BusinessExceptionHandler(ErrorCode.PASSWORDS_DO_NOT_MATCH);
            }
            member.setPassword(passwordEncoder.encode(memberUpdateDto.getPassword()));
        }
        if (memberUpdateDto.getUrlLink() != null) {
            member.setUrlLink(memberUpdateDto.getUrlLink());
        }

        member.setMemberRoleStatus(member.getMemberRoleStatus());
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

    private PublisherDto convertToPublisherDto(Publisher publisher) {
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
                .memberRoleStatus(publisher.getMemberList().get(0).getMemberRoleStatus())
                .build();
    }
}
