package com.lit_map_BackEnd.domain.work.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkCategoryGenre is a Querydsl query type for WorkCategoryGenre
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkCategoryGenre extends EntityPathBase<WorkCategoryGenre> {

    private static final long serialVersionUID = 919812878L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkCategoryGenre workCategoryGenre = new QWorkCategoryGenre("workCategoryGenre");

    public final com.lit_map_BackEnd.domain.category.entity.QCategory category;

    public final com.lit_map_BackEnd.domain.genre.entity.QGenre genre;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QWork work;

    public QWorkCategoryGenre(String variable) {
        this(WorkCategoryGenre.class, forVariable(variable), INITS);
    }

    public QWorkCategoryGenre(Path<? extends WorkCategoryGenre> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkCategoryGenre(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkCategoryGenre(PathMetadata metadata, PathInits inits) {
        this(WorkCategoryGenre.class, metadata, inits);
    }

    public QWorkCategoryGenre(Class<? extends WorkCategoryGenre> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.lit_map_BackEnd.domain.category.entity.QCategory(forProperty("category")) : null;
        this.genre = inits.isInitialized("genre") ? new com.lit_map_BackEnd.domain.genre.entity.QGenre(forProperty("genre")) : null;
        this.work = inits.isInitialized("work") ? new QWork(forProperty("work"), inits.get("work")) : null;
    }

}

