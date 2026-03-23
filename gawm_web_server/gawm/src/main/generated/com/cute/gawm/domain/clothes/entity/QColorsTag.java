package com.cute.gawm.domain.clothes.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QColorsTag is a Querydsl query type for ColorsTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QColorsTag extends EntityPathBase<ColorsTag> {

    private static final long serialVersionUID = 1358754351L;

    public static final QColorsTag colorsTag = new QColorsTag("colorsTag");

    public final StringPath colorCode = createString("colorCode");

    public final StringPath name = createString("name");

    public QColorsTag(String variable) {
        super(ColorsTag.class, forVariable(variable));
    }

    public QColorsTag(Path<? extends ColorsTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QColorsTag(PathMetadata metadata) {
        super(ColorsTag.class, metadata);
    }

}

