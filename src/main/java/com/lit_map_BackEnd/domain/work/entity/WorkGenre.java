package com.lit_map_BackEnd.domain.work.entity;

import com.lit_map_BackEnd.domain.genre.entity.Genre;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class WorkGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_genre_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "work_id")
    private Work work;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

}
