package com.lit_map_BackEnd.domain.work.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkAuthor is a Querydsl query type for WorkAuthor
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkAuthor extends EntityPathBase<WorkAuthor> {

    private static final long serialVersionUID = -1029581374L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkAuthor workAuthor = new QWorkAuthor("workAuthor");

    public final com.lit_map_BackEnd.domain.author.entity.QAuthor author;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QWork work;

    public QWorkAuthor(String variable) {
        this(WorkAuthor.class, forVariable(variable), INITS);
    }

    public QWorkAuthor(Path<? extends WorkAuthor> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkAuthor(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkAuthor(PathMetadata metadata, PathInits inits) {
        this(WorkAuthor.class, metadata, inits);
    }

    public QWorkAuthor(Class<? extends WorkAuthor> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.lit_map_BackEnd.domain.author.entity.QAuthor(forProperty("author")) : null;
        this.work = inits.isInitialized("work") ? new QWork(forProperty("work"), inits.get("work")) : null;
    }

}

