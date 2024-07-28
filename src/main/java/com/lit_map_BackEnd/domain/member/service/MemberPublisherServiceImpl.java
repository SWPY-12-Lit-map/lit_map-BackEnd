
package com.lit_map_BackEnd.domain.member.service;

import com.lit_map_BackEnd.common.exception.BusinessExceptionHandler;
import com.lit_map_BackEnd.common.exception.code.ErrorCode;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.category.repository.CategoryRepository;
import com.lit_map_BackEnd.domain.member.dto.*;
import com.lit_map_BackEnd.domain.member.entity.CustomUserDetails;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.Publisher;
import com.lit_map_BackEnd.domain.member.entity.MemberRoleStatus;
import com.lit_map_BackEnd.domain.member.repository.MemberRepository;
import com.lit_map_BackEnd.domain.member.repository.PublisherRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
    private final EmailService emailService;
    private final HttpSession session;
    private final RestTemplate restTemplate = new RestTemplate();
    private final CustomUserDetailsService customUserDetailsService;

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
                .memberRoleStatus(MemberRoleStatus.PENDING_MEMBER) // 기본값 설정
                .build();

        Member savedMember = memberRepository.save(member);

        // (1인작가) 회원 가입 대기 이메일 전송
        String subject = "[litmap] 회원 가입 대기";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,iVBORw0qMPdgAAAAASUVORK5CYII=\\\"/>"
                + "<br><h2>회원 가입 완료</h2><h4>"
                + memberDto.getName()
                + "님 회원 가입이 완료되었습니다. 관리자의 승인을 기다려주세요.</h4></div>";

        emailService.sendEmail(memberDto.getLitmapEmail(), subject, content);
        return savedMember;
    } // 1인작가 회원가입

    @Override
    @Transactional
    public Member approveMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        member.setMemberRoleStatus(MemberRoleStatus.ACTIVE_MEMBER);
        memberRepository.save(member);

        // 승인 이메일 전송
        String subject = "[litmap] 회원 가입 승인";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,iVBORw0qMPdgAAAAASUVORK5CYII=\\\"/>"
                + "<br><h2>회원 가입 승인</h2><h4>"
                + member.getName()
                + "님 회원 가입이 승인되었습니다. 환영합니다!</h4></div>";

        emailService.sendEmail(member.getLitmapEmail(), subject, content);
        return member;
    }

    public boolean checkLitmapEmailExists(String litmapEmail) {
        return memberRepository.findByLitmapEmail(litmapEmail).isPresent();
    }

    @Override
    @Transactional
    public Publisher savePublisher(PublisherDto publisherDto) {

        if (memberRepository.findByLitmapEmail(publisherDto.getLitmapEmail()).isPresent()) {
            throw new BusinessExceptionHandler(ErrorCode.DUPLICATE_EMAIL);
        }

        Publisher publisher = Publisher.builder()
                .publisherNumber(publisherDto.getPublisherNumber())
                .publisherName(publisherDto.getPublisherName())
                .publisherAddress(publisherDto.getPublisherAddress())
                .publisherPhoneNumber(publisherDto.getPublisherPhoneNumber())
                .publisherCeo(publisherDto.getPublisherCeo())
                .memberList(new ArrayList<>()) // memberList 초기화
                .build();

        Member member = Member.builder()
                .litmapEmail(publisherDto.getLitmapEmail())
                .name(publisherDto.getName())
                .password(passwordEncoder.encode(publisherDto.getPassword()))
                .nickname(publisherDto.getNickname())
                .myMessage(publisherDto.getMyMessage())
                .userImage(publisherDto.getUserImage())
                .publisher(publisher)
                .memberRoleStatus(MemberRoleStatus.ACTIVE_MEMBER)
                .build();

        publisher.getMemberList().add(member);

        Publisher savedPublisher = publisherRepository.save(publisher);

        // (출판사) 회원 가입 승인 이메일 전송
        String subject = "[litmap] 회원 가입 승인";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,iVBORw0qMPdgAAAAASUVORK5CYII=\\\"/>"
                + "<br><h2>회원 가입 완료</h2><h4>"
                + publisherDto.getPublisherName()
                + "님 회원 가입 승인되었습니다.</h4></div>";

        emailService.sendEmail(publisherDto.getLitmapEmail(), subject, content);

        // 출판사 직원 로그인 상태 유지
        loginAfterRegistration(publisherDto.getLitmapEmail(), publisherDto.getPassword());
        return savedPublisher;
    } // 출판사 회원가입

    private void loginAfterRegistration(String email, String password) {
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    @Override
    public Member findByLitmapEmail(String litmapEmail) {
        return memberRepository.findByLitmapEmail(litmapEmail)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Member login(String litmapEmail, String password) {
        Optional<Member> memberOptional = memberRepository.findByLitmapEmail(litmapEmail);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (passwordEncoder.matches(password, member.getPassword())) {
                //session.setAttribute("member", member);
                session.setAttribute("loggedInUser", new CustomUserDetails(member)); // 세션에 사용자 정보 저장
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
        String subject = "[litmap] 임시 비밀번호 발송";
        String content = "<div style=\"margin:30px;\"><img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAI4AAAAyCAYAAACK9eMGAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAcCSURBVHgB7Zx5bBRlH8c/M7tbXrooqLy+8HqVHsD7+h7etxGPeESNMdF4RJF4K5F4sG0Bj9Ug0G01aDzAqFH/0KgkSEyMGg8S0XhGjagVtlgPIioaiAKl3Z3xOz0MrbO0pbPXzHyS6dzTmWe/z+/3PL/nMAhxJQnmaKZMsLH/bsLuYJnO8TibP5rJz78TcKIpar/DY2w4uoH095QRSaZFK/n+WKnjVAumGfB/yMYHXreF+EQGCKeZmmdtjGPwno0W1g0m5jPkCX3vU+9x8O3P83x2OPdFlUD74jE25UOSqnGVmLMMvrsWjInOuxvDe4S+19g9H+ko/kbP+3j+bD3zRwvjptmslSjTDJcoASWpzBan7hb97HO1O45AYb+2nYrp8/jyB3aRQApnEdX7y/wvVQKeTsCQRV1QT9s8RkjghJNi0vEGxjJt7k2AkGvaKNFcWU96BR4QKOE0U3uJEu8xbVYQKIy3KrAvnuVhhSUwwlHt8VQZ6idlbUyCw2/KKHPqWfsgHhMI4SxkapVB5mkCJBq5ps87yZw3l/ZW8oDvEzKpb4yQeV2bexEcFpt0HJ4v0Tj43uJUUjNTua+aYLBNMSXHNd1HnvG1cHpd1P0EAJVl3lXE+8pG1n5OAfC1q4qQvQP/Y6s9bf4HHHxcI+mCiMbBtxbnHv6zn0XHdPyNU72+VAG9ldBGIfGtxbHZfib+tqgruuCQBOmVFAHfWhyZ74vwIYpD/WqRTdSz7nGKiC+F43SRkBU/Ep+hAvA6sM6RaFZTZHxpyuOsP1SrUfgIhRQetbDkmtqKLhoHX1ocG0vV8OH2qilNKqjYnKHzikSRXdNAfCkcE/MAu6y6k+XmZlo/0+ozSgxfuioLO05IXvGlcMzAdZsoPEHqYhDiIaFwQnaJUDghu0RZC+d+akelqJpASMEpW+EsYJ+9OuDNCBUHElJwyjKOM5+a/WIYr2pzKiFFoeyE47gmRYVfJBRNUSkrV3Uf1XUG0XfoHtcdUkzKRjgpag/pxFilzUmEFJ2yEE6K6rPUZLlSbcSBGn1ZypS8cJqouVplGmfY6m6ElAwlXThuoabexmgipBu56xNkdcfv7JqtdL2epH1TE7X7ykovNLBXJWhb2nd+EbUHylqMd7838+lospUmoy7V7tcJ0s85RQSwb9D+y/W0Pdt3bckKp5naBht7ISE70mgMMsNGnNhRWr1nYo+TyC6h5zf+UzgSTYsE5fqM0ZiH9zqhRc5UKFo/Z2PuYWLN0PYmLaUtnBbqUhJNAp90xvIKi+hig07X2SZMjBuVXlMYIkrflIoAW3t3z9JyGMOgpITjNCFsh0f0UX4f1rJLNNL6Sq5zcinnKJsNWTgZYqm5tP7Sc2+NExvrJxwVEapl9e+QXF1HwZaMcBZRPbYDe7k+4ERCXNEP/LDS56gcp6vwkN5h08lc50uiVnU3/5oYxXw7FM1gGFX6cxCOwegpc+y4fKJlpdJwM97wRoaMYmbG5W4ni25xnPHdEdUE7OBMDDBibDJn19O+gREQI3NXMzV9ZRyXGVNtaw7t7U1M/tZ06b9dVOE0M0WKzrypzQMIGTI2kdNS1OWcazmK8W6WQWefvX4klY+iCUei+Z9Eo8KeEfanGSaqQT2xs0mBVbC9UCvXCQiyRK+LkHWdZXUb29aMIVZlU7Ha7hmX7kzcsEnPWy0X2G+G0qIIp5m6U/QJyySasYQMGVma25TZBp375jf4MA6uGXIOre3OWoHA0yLYEkl2xQC394WW//btdBH7JUrXAxZ2v4GABReOosGX6SUeMcKRCMOmga8+dDuepGpCJREnWLpR0d2Ec0zlF6fik1QAz3Xkp0Qzg27LFFufZNrL5CDG+jpZnCX6vRZr9+2+4wUVjqqTs/USzWFYz1vGYNYqXWdo81fFwm6dRXq7mhl+0v6dg92rSPSLcXJPRprLIRZMOHJP8/UaI56YOaQ/yowXWBgtvZlxT8XCXmph0uWz+fqbodwvYbwg+eSsoUlYVbg0URREOIpqyozajYR4ghNh78Q+w+5uZkANn92zcsmdGNO0nKSyULsE9ZRF5N5G1ny686fZS+tJ53RVqo6frLaqwgrnfLnSIxTt1OZVhIyYB/n3mC10zuuAaySQPWQPVCXPPqSK95JG1nWPL1fZ5lwdv1bnp0ewpqvZ4GMLFjSQXub+VONOZeyZuf+rtafb0bwJp4nxu5mMfUYvdiYhnjCTL36Xy1cYg2+dRsqtdC1xulDseI3KNsu1Wn4PUycrUJjQdU4nuMq/Ps1UhNnaoHP707PsjA0qZPeLGxktTD4Oj5nNmlVS/t76Z/8kzyjx2gcmnhNYlG/ehzzgfNvAYwrGHWR0d2PwFgtzawNr3t/xWDP/iCf4cctQn7GUQ2PX8FEXHvMH4YDqO9qMPdgAAAAASUVORK5CYII=\\\"/>"
                + "<br><br><h2>비밀번호 확인</h2><h4>고객님의 임시 비밀번호를 아래에서 확인하세요.</h4><br><br><br><h4>"
                + tempPw
                + "</h4><br><button style=\\\"background-color: #1890FF; color: white; font-size: 16px; border: none; border-radius: 5px; padding: 10px 20px;\\\">복사하기</button></div>";

        // 이메일 전송
        emailService.sendEmail(member.getLitmapEmail(), subject, content);  // 수신자를 litmapEmail로 설정

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
    }// 사업자 api로 변경하기

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
        return memberRepository.save(member);
    } // 1인 작가 마이페이지 수정

    @Override
    @Transactional
    public Member updatePublisherMember(String litmapEmail, PublisherUpdateDto publisherUpdateDto) {
        Member member = memberRepository.findByLitmapEmail(litmapEmail)
                .orElseThrow(() -> new BusinessExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Publisher publisher = member.getPublisher();
        if (publisher == null) {
            throw new BusinessExceptionHandler(ErrorCode.PUBLISHER_NOT_FOUND);
        }
        updatePublisherFields(publisher, publisherUpdateDto);
        updateMemberFields(member, publisherUpdateDto);
        memberRepository.save(member); // 출판사 직원 정보를 업데이트할 때, memberRepository에 저장
        return member;
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
        if (memberUpdateDto.getUrlLink() != null) {
            member.setUrlLink(memberUpdateDto.getUrlLink());
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
