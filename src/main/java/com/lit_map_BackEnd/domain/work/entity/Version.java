package com.lit_map_BackEnd.domain.work.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lit_map_BackEnd.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    private Double versionNum;
    private String versionName;

    @Column(name = "relationship", columnDefinition = "longtext")
    private String relationship;

    @Enumerated(EnumType.STRING)
    private Confirm confirm;

    public void changeRelationship(Map<String, Object> relationship) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        relationship.remove("workId");
        relationship.remove("version");
        this.relationship = objectMapper.writeValueAsString(relationship);
    }
}
