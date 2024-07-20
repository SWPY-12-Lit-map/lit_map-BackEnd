package com.lit_map_BackEnd.domain.member.entity;

import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.work.entity.Work;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publisher_id")
    private Long id;

    private Long publisherNumber; // 사업자 번호
    private String publisherName; // 출판사 이름

    @Setter
    private String publisherAddress; // 출판사 주소

    @Setter
    private String publisherPhoneNumber; // 출판사 연락처

    @Setter
    private String publisherCeo; // 대표자 이름

    @Setter
    private boolean withdrawalRequested = false;  // 탈퇴 요청 여부

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Member> memberList = new ArrayList<>();

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Work> workList = new ArrayList<>();
}
