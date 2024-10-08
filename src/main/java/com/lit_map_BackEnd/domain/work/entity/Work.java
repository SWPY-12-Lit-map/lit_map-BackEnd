package com.lit_map_BackEnd.domain.work.entity;

import com.lit_map_BackEnd.common.entity.BaseTimeEntity;
import com.lit_map_BackEnd.domain.category.entity.Category;
import com.lit_map_BackEnd.domain.character.entity.Cast;
import com.lit_map_BackEnd.domain.member.entity.Member;
import com.lit_map_BackEnd.domain.member.entity.Publisher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "work")
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
    private List<WorkCategoryGenre> workCategoryGenres = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
    private List<WorkAuthor> workAuthors = new ArrayList<>();

    private String mainAuthor;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    private LocalDateTime publisherDate;

    private String imageUrl;

    private String title;

    @Lob
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
    private List<Version> versions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
    private List<RollBackVersion> rollBackVersions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
    private List<Cast> casts = new ArrayList<>();

    private int view;

    public void changeCategory(Category category) {
        this.category = category;
    }

    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void mainAuthorSetting(String mainAuthor) {
        this.mainAuthor = mainAuthor;
    }
}
