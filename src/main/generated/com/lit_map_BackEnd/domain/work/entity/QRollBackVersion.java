package com.lit_map_BackEnd.domain.work.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRollBackVersion is a Querydsl query type for RollBackVersion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRollBackVersion extends EntityPathBase<RollBackVersion> {

    private static final long serialVersionUID = -793809106L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRollBackVersion rollBackVersion = new QRollBackVersion("rollBackVersion");

    public final com.lit_map_BackEnd.common.entity.QBaseTimeEntity _super = new com.lit_map_BackEnd.common.entity.QBaseTimeEntity(this);

    public final ListPath<com.lit_map_BackEnd.domain.character.entity.RollBackCast, com.lit_map_BackEnd.domain.character.entity.QRollBackCast> casts = this.<com.lit_map_BackEnd.domain.character.entity.RollBackCast, com.lit_map_BackEnd.domain.character.entity.QRollBackCast>createList("casts", com.lit_map_BackEnd.domain.character.entity.RollBackCast.class, com.lit_map_BackEnd.domain.character.entity.QRollBackCast.class, PathInits.DIRECT2);

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

    public QRollBackVersion(String variable) {
        this(RollBackVersion.class, forVariable(variable), INITS);
    }

    public QRollBackVersion(Path<? extends RollBackVersion> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRollBackVersion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRollBackVersion(PathMetadata metadata, PathInits inits) {
        this(RollBackVersion.class, metadata, inits);
    }

    public QRollBackVersion(Class<? extends RollBackVersion> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.work = inits.isInitialized("work") ? new QWork(forProperty("work"), inits.get("work")) : null;
    }

}

