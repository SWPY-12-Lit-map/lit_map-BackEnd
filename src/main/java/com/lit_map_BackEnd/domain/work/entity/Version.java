package com.lit_map_BackEnd.domain.work.entity;

import com.lit_map_BackEnd.common.converter.JsonMapConverter;
import com.lit_map_BackEnd.common.entity.BaseTimeEntity;
import com.lit_map_BackEnd.domain.character.entity.Cast;
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
@Table(name = "work_version")
public class Version extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "work_id")
    private Work work;

    @Builder.Default
    @OneToMany(mappedBy = "version", cascade = CascadeType.ALL)
    private List<Cast> casts = new ArrayList<>();

    private Double versionNum;
    private String versionName;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "relationship", columnDefinition = "json")
    private Map<String, Object> relationship;

    @Enumerated(EnumType.STRING)
    private Confirm confirm;

    public void changeRelationship(Map<String, Object> relationship) {
        this.relationship = relationship;
    }

    public void updateVersion(String versionName, Map<String, Object> relationship) {
        this.versionName = versionName;
        this.relationship = relationship;
    }

    public void confirmSetting(Confirm confirm) {
        this.confirm = confirm;
    }

}
