package com.lit_map_BackEnd.domain.member.entity;

import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.work.entity.Work;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Data
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String litmapEmail;

    @Column(nullable = true, unique = true)
    private String workEmail; // 업무용 이메일, 1인작가 아이디 찾기

    private String name;

    @Setter
    private String password;

    @Column(length = 8, nullable = false)
    private String nickname;

    private String myMessage;

    private String userImage; // 사용자 이미지 등록, 옵션임 - 등록해도 되고 안 해도 됨

    private String urlLink; // 판매 링크 사이트

    @Setter
    private boolean withdrawalRequested; // 탈퇴 요청 여부

    @Schema(defaultValue = "PENDING_MEMBER")
    private Role role;

    // 회원 한 명당 카테고리 여러 개 선택
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Builder.Default
//    private List<Category> categoryList = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // 회원 여러 명 : 출판사 한 개
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    // 회원 한 명 : 작품 여러 개
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Work> works = new ArrayList<>();

    // 어드민 auth
    // create_data
    // update_data
}
