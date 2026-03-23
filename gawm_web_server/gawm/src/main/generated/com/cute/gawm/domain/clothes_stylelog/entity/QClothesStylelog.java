package com.cute.gawm.domain.clothes_stylelog.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClothesStylelog is a Querydsl query type for ClothesStylelog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClothesStylelog extends EntityPathBase<ClothesStylelog> {

    private static final long serialVersionUID = -1810938454L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QClothesStylelog clothesStylelog = new QClothesStylelog("clothesStylelog");

    public final com.cute.gawm.common.QBaseEntity _super = new com.cute.gawm.common.QBaseEntity(this);

    public final com.cute.gawm.domain.clothes.entity.QClothes clothes;

    public final NumberPath<Integer> clothesStylelogId = createNumber("clothesStylelogId", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final NumberPath<Double> rotate = createNumber("rotate", Double.class);

    public final NumberPath<Double> size = createNumber("size", Double.class);

    public final com.cute.gawm.domain.stylelog.entity.QStylelog stylelog;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Double> x = createNumber("x", Double.class);

    public final NumberPath<Double> y = createNumber("y", Double.class);

    public QClothesStylelog(String variable) {
        this(ClothesStylelog.class, forVariable(variable), INITS);
    }

    public QClothesStylelog(Path<? extends ClothesStylelog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QClothesStylelog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QClothesStylelog(PathMetadata metadata, PathInits inits) {
        this(ClothesStylelog.class, metadata, inits);
    }

    public QClothesStylelog(Class<? extends ClothesStylelog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.clothes = inits.isInitialized("clothes") ? new com.cute.gawm.domain.clothes.entity.QClothes(forProperty("clothes"), inits.get("clothes")) : null;
        this.stylelog = inits.isInitialized("stylelog") ? new com.cute.gawm.domain.stylelog.entity.QStylelog(forProperty("stylelog"), inits.get("stylelog")) : null;
    }

}

