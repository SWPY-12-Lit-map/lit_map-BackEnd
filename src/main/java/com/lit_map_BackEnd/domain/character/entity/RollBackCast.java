package com.lit_map_BackEnd.domain.character.entity;

import com.lit_map_BackEnd.domain.work.entity.RollBackVersion;
import com.lit_map_BackEnd.domain.work.entity.Version;
import com.lit_map_BackEnd.domain.work.entity.Work;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "rollback_casts")
public class RollBackCast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "work_id")
    private Work work;

    @ManyToOne
    @JoinColumn(name = "rollback_version_id")
    private RollBackVersion rollBackVersion;

    private String imageUrl;

    private String name;

    private String role;

    private String type;

    private String gender;
    private int age;
    private String mbti;

    @Lob
    private String contents;

    public void changeRollBackVersion(RollBackVersion rollBackVersion) {
        this.rollBackVersion = rollBackVersion;
    }
}
