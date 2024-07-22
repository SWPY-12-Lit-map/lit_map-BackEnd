package com.lit_map_BackEnd.domain.work.entity;

import com.lit_map_BackEnd.common.converter.JsonMapConverter;
import com.lit_map_BackEnd.common.entity.BaseTimeEntity;
import com.lit_map_BackEnd.domain.character.entity.Cast;
import com.lit_map_BackEnd.domain.character.entity.RollBackCast;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "rollback_versions")
public class RollBackVersion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "work_id")
    private Work work;

    @Builder.Default
    @OneToMany(mappedBy = "rollBackVersion", cascade = CascadeType.ALL)
    private List<RollBackCast> casts = new ArrayList<>();

    private Double versionNum;
    private String versionName;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "relationship", columnDefinition = "json")
    private Map<String, Object> relationship;

    @Enumerated(EnumType.STRING)
    private Confirm confirm;
}
