package com.cute.gawm.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 385455389L;

    public static final QUser user = new QUser("user");

    public final NumberPath<Integer> age = createNumber("age", Integer.class);

    public final StringPath email = createString("email");

    public final EnumPath<User.Gender> gender = createEnum("gender", User.Gender.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final StringPath profileImg = createString("profileImg");

    public final EnumPath<User.Provider> provider = createEnum("provider", User.Provider.class);

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final StringPath session = createString("session");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

