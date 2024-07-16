package com.lit_map_BackEnd.domain.work.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkGenre is a Querydsl query type for WorkGenre
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkGenre extends EntityPathBase<WorkGenre> {

    private static final long serialVersionUID = -2106363284L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkGenre workGenre = new QWorkGenre("workGenre");

    public final com.lit_map_BackEnd.domain.genre.entity.QGenre genre;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QWork work;

    public QWorkGenre(String variable) {
        this(WorkGenre.class, forVariable(variable), INITS);
    }

    public QWorkGenre(Path<? extends WorkGenre> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkGenre(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkGenre(PathMetadata metadata, PathInits inits) {
        this(WorkGenre.class, metadata, inits);
    }

    public QWorkGenre(Class<? extends WorkGenre> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.genre = inits.isInitialized("genre") ? new com.lit_map_BackEnd.domain.genre.entity.QGenre(forProperty("genre")) : null;
        this.work = inits.isInitialized("work") ? new QWork(forProperty("work"), inits.get("work")) : null;
    }

}

