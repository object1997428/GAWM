package com.cute.gawm.domain.clothes_lookbook.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClothesLookbook is a Querydsl query type for ClothesLookbook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClothesLookbook extends EntityPathBase<ClothesLookbook> {

    private static final long serialVersionUID = -918929910L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QClothesLookbook clothesLookbook = new QClothesLookbook("clothesLookbook");

    public final com.cute.gawm.common.QBaseEntity _super = new com.cute.gawm.common.QBaseEntity(this);

    public final com.cute.gawm.domain.clothes.entity.QClothes clothes;

    public final NumberPath<Integer> clothesLookbookId = createNumber("clothesLookbookId", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final BooleanPath isDeleted = _super.isDeleted;

    public final com.cute.gawm.domain.lookbook.entity.QLookbook lookbook;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QClothesLookbook(String variable) {
        this(ClothesLookbook.class, forVariable(variable), INITS);
    }

    public QClothesLookbook(Path<? extends ClothesLookbook> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QClothesLookbook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QClothesLookbook(PathMetadata metadata, PathInits inits) {
        this(ClothesLookbook.class, metadata, inits);
    }

    public QClothesLookbook(Class<? extends ClothesLookbook> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.clothes = inits.isInitialized("clothes") ? new com.cute.gawm.domain.clothes.entity.QClothes(forProperty("clothes"), inits.get("clothes")) : null;
        this.lookbook = inits.isInitialized("lookbook") ? new com.cute.gawm.domain.lookbook.entity.QLookbook(forProperty("lookbook"), inits.get("lookbook")) : null;
    }

}

