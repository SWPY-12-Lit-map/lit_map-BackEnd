package com.lit_map_BackEnd.domain.character.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRollBackCast is a Querydsl query type for RollBackCast
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRollBackCast extends EntityPathBase<RollBackCast> {

    private static final long serialVersionUID = -1316244719L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRollBackCast rollBackCast = new QRollBackCast("rollBackCast");

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final StringPath contents = createString("contents");

    public final StringPath gender = createString("gender");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final StringPath mbti = createString("mbti");

    public final StringPath name = createString("name");

    public final StringPath role = createString("role");

    public final com.lit_map_BackEnd.domain.work.entity.QRollBackVersion rollBackVersion;

    public final StringPath type = createString("type");

    public final com.lit_map_BackEnd.domain.work.entity.QWork work;

    public QRollBackCast(String variable) {
        this(RollBackCast.class, forVariable(variable), INITS);
    }

    public QRollBackCast(Path<? extends RollBackCast> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRollBackCast(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRollBackCast(PathMetadata metadata, PathInits inits) {
        this(RollBackCast.class, metadata, inits);
    }

    public QRollBackCast(Class<? extends RollBackCast> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.rollBackVersion = inits.isInitialized("rollBackVersion") ? new com.lit_map_BackEnd.domain.work.entity.QRollBackVersion(forProperty("rollBackVersion"), inits.get("rollBackVersion")) : null;
        this.work = inits.isInitialized("work") ? new com.lit_map_BackEnd.domain.work.entity.QWork(forProperty("work"), inits.get("work")) : null;
    }

}

