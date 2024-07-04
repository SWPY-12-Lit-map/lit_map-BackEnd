package com.lit_map_BackEnd.domain.work.entity;

import com.lit_map_BackEnd.common.entity.BaseTimeEntity;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Work extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder.Default
    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
    private List<WorkGenre> workGenres = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
    private List<WorkAuthor> workAuthors = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String imageUrl;
    private String title;

    @Lob
    private String content;

    private int view;
}
