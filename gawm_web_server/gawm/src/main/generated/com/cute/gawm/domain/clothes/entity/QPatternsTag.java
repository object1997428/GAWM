package com.cute.gawm.domain.clothes.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPatternsTag is a Querydsl query type for PatternsTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPatternsTag extends EntityPathBase<PatternsTag> {

    private static final long serialVersionUID = 851171996L;

    public static final QPatternsTag patternsTag = new QPatternsTag("patternsTag");

    public final StringPath name = createString("name");

    public QPatternsTag(String variable) {
        super(PatternsTag.class, forVariable(variable));
    }

    public QPatternsTag(Path<? extends PatternsTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPatternsTag(PathMetadata metadata) {
        super(PatternsTag.class, metadata);
    }

}

