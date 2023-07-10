package com.junha.securemarshmallow.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserAccount is a Querydsl query type for UserAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAccount extends EntityPathBase<UserAccount> {

    private static final long serialVersionUID = -1596141907L;

    public static final QUserAccount userAccount = new QUserAccount("userAccount");

    public final StringPath email = createString("email");

    public final StringPath nickname = createString("nickname");

    public final StringPath refreshToken = createString("refreshToken");

    public final StringPath userId = createString("userId");

    public final StringPath userPassword = createString("userPassword");

    public QUserAccount(String variable) {
        super(UserAccount.class, forVariable(variable));
    }

    public QUserAccount(Path<? extends UserAccount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserAccount(PathMetadata metadata) {
        super(UserAccount.class, metadata);
    }

}

