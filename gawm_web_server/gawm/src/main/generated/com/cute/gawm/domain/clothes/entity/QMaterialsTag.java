package com.cute.gawm.domain.clothes.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMaterialsTag is a Querydsl query type for MaterialsTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMaterialsTag extends EntityPathBase<MaterialsTag> {

    private static final long serialVersionUID = -1317307511L;

    public static final QMaterialsTag materialsTag = new QMaterialsTag("materialsTag");

    public final StringPath name = createString("name");

    public QMaterialsTag(String variable) {
        super(MaterialsTag.class, forVariable(variable));
    }

    public QMaterialsTag(Path<? extends MaterialsTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMaterialsTag(PathMetadata metadata) {
        super(MaterialsTag.class, metadata);
    }

}

