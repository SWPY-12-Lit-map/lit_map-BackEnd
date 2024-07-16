package com.lit_map_BackEnd.domain.character.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCast is a Querydsl query type for Cast
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCast extends EntityPathBase<Cast> {

    private static final long serialVersionUID = -1411810323L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCast cast = new QCast("cast");

    public final com.lit_map_BackEnd.common.entity.QBaseTimeEntity _super = new com.lit_map_BackEnd.common.entity.QBaseTimeEntity(this);

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final StringPath contents = createString("contents");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath gender = createString("gender");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final StringPath mbti = createString("mbti");

    public final StringPath name = createString("name");

    public final StringPath role = createString("role");

    public final StringPath type = createString("type");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final com.lit_map_BackEnd.domain.work.entity.QVersion version;

    public final com.lit_map_BackEnd.domain.work.entity.QWork work;

    public QCast(String variable) {
        this(Cast.class, forVariable(variable), INITS);
    }

    public QCast(Path<? extends Cast> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCast(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCast(PathMetadata metadata, PathInits inits) {
        this(Cast.class, metadata, inits);
    }

    public QCast(Class<? extends Cast> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.version = inits.isInitialized("version") ? new com.lit_map_BackEnd.domain.work.entity.QVersion(forProperty("version"), inits.get("version")) : null;
        this.work = inits.isInitialized("work") ? new com.lit_map_BackEnd.domain.work.entity.QWork(forProperty("work"), inits.get("work")) : null;
    }

}

