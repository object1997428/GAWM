package com.cute.gawm.domain.lookbook_image.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLookbookImage is a Querydsl query type for LookbookImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLookbookImage extends EntityPathBase<LookbookImage> {

    private static final long serialVersionUID = -654063448L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLookbookImage lookbookImage = new QLookbookImage("lookbookImage");

    public final StringPath image = createString("image");

    public final com.cute.gawm.domain.lookbook.entity.QLookbook lookbook;

    public final NumberPath<Integer> lookbookImageId = createNumber("lookbookImageId", Integer.class);

    public QLookbookImage(String variable) {
        this(LookbookImage.class, forVariable(variable), INITS);
    }

    public QLookbookImage(Path<? extends LookbookImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLookbookImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLookbookImage(PathMetadata metadata, PathInits inits) {
        this(LookbookImage.class, metadata, inits);
    }

    public QLookbookImage(Class<? extends LookbookImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lookbook = inits.isInitialized("lookbook") ? new com.cute.gawm.domain.lookbook.entity.QLookbook(forProperty("lookbook"), inits.get("lookbook")) : null;
    }

}

