package com.lit_map_BackEnd.domain.work.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWork is a Querydsl query type for Work
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWork extends EntityPathBase<Work> {

    private static final long serialVersionUID = -1506712233L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWork work = new QWork("work");

    public final com.lit_map_BackEnd.common.entity.QBaseTimeEntity _super = new com.lit_map_BackEnd.common.entity.QBaseTimeEntity(this);

    public final ListPath<com.lit_map_BackEnd.domain.character.entity.Cast, com.lit_map_BackEnd.domain.character.entity.QCast> casts = this.<com.lit_map_BackEnd.domain.character.entity.Cast, com.lit_map_BackEnd.domain.character.entity.QCast>createList("casts", com.lit_map_BackEnd.domain.character.entity.Cast.class, com.lit_map_BackEnd.domain.character.entity.QCast.class, PathInits.DIRECT2);

    public final com.lit_map_BackEnd.domain.category.entity.QCategory category;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final com.lit_map_BackEnd.domain.member.entity.QMember member;

    public final DateTimePath<java.time.LocalDateTime> publisherDate = createDateTime("publisherDate", java.time.LocalDateTime.class);

    public final StringPath publisherName = createString("publisherName");

    public final ListPath<RollBackVersion, QRollBackVersion> rollBackVersions = this.<RollBackVersion, QRollBackVersion>createList("rollBackVersions", RollBackVersion.class, QRollBackVersion.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

    public final ListPath<Version, QVersion> versions = this.<Version, QVersion>createList("versions", Version.class, QVersion.class, PathInits.DIRECT2);

    public final NumberPath<Integer> view = createNumber("view", Integer.class);

    public final ListPath<WorkAuthor, QWorkAuthor> workAuthors = this.<WorkAuthor, QWorkAuthor>createList("workAuthors", WorkAuthor.class, QWorkAuthor.class, PathInits.DIRECT2);

    public final ListPath<WorkCategoryGenre, QWorkCategoryGenre> workCategoryGenres = this.<WorkCategoryGenre, QWorkCategoryGenre>createList("workCategoryGenres", WorkCategoryGenre.class, QWorkCategoryGenre.class, PathInits.DIRECT2);

    public final ListPath<WorkGenre, QWorkGenre> workGenres = this.<WorkGenre, QWorkGenre>createList("workGenres", WorkGenre.class, QWorkGenre.class, PathInits.DIRECT2);

    public QWork(String variable) {
        this(Work.class, forVariable(variable), INITS);
    }

    public QWork(Path<? extends Work> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWork(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWork(PathMetadata metadata, PathInits inits) {
        this(Work.class, metadata, inits);
    }

    public QWork(Class<? extends Work> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.lit_map_BackEnd.domain.category.entity.QCategory(forProperty("category")) : null;
        this.member = inits.isInitialized("member") ? new com.lit_map_BackEnd.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

