package com.lit_map_BackEnd.domain.character.entity;

import com.lit_map_BackEnd.common.entity.BaseTimeEntity;
import com.lit_map_BackEnd.domain.character.dto.CastRequestDto;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "cast")
public class Cast extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cast_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "work_id")
    private Work work;

    @ManyToOne
    @JoinColumn(name = "version_id")
    private Version version;

    private String imageUrl;

    private String name;

    private String role;

    private String type;

    private String gender;
    private int age;
    private String mbti;

    @Lob
    private String contents;

    public void changeState(CastRequestDto castRequestDto, String image) {
        this.imageUrl = image;
        this.role = castRequestDto.getRole();
        this.name = castRequestDto.getName();
        this.type = castRequestDto.getType();
        this.gender = castRequestDto.getGender();
        this.age = castRequestDto.getAge();
        this.mbti = castRequestDto.getMbti();
        this.contents = castRequestDto.getContents();
    }
}
