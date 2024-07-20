package com.lit_map_BackEnd.domain.work.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVersion is a Querydsl query type for Version
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVersion extends EntityPathBase<Version> {

    private static final long serialVersionUID = -934375214L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVersion version = new QVersion("version");

    public final com.lit_map_BackEnd.common.entity.QBaseTimeEntity _super = new com.lit_map_BackEnd.common.entity.QBaseTimeEntity(this);

    public final ListPath<com.lit_map_BackEnd.domain.character.entity.Cast, com.lit_map_BackEnd.domain.character.entity.QCast> casts = this.<com.lit_map_BackEnd.domain.character.entity.Cast, com.lit_map_BackEnd.domain.character.entity.QCast>createList("casts", com.lit_map_BackEnd.domain.character.entity.Cast.class, com.lit_map_BackEnd.domain.character.entity.QCast.class, PathInits.DIRECT2);

    public final EnumPath<Confirm> confirm = createEnum("confirm", Confirm.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final MapPath<String, Object, SimplePath<Object>> relationship = this.<String, Object, SimplePath<Object>>createMap("relationship", String.class, Object.class, SimplePath.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final StringPath versionName = createString("versionName");

    public final NumberPath<Double> versionNum = createNumber("versionNum", Double.class);

    public final QWork work;

    public QVersion(String variable) {
        this(Version.class, forVariable(variable), INITS);
    }

    public QVersion(Path<? extends Version> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVersion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVersion(PathMetadata metadata, PathInits inits) {
        this(Version.class, metadata, inits);
    }

    public QVersion(Class<? extends Version> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.work = inits.isInitialized("work") ? new QWork(forProperty("work"), inits.get("work")) : null;
    }

}

