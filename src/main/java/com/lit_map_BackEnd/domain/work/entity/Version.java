package com.lit_map_BackEnd.domain.work.entity;

import com.lit_map_BackEnd.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    private Double versionNum;
    private String versionName;
    private String relationShip;

    @Enumerated(EnumType.STRING)
    private Confirm confirm;
}
