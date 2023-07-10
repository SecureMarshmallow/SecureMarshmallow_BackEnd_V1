package com.junha.securemarshmallow.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileData is a Querydsl query type for FileData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFileData extends EntityPathBase<FileData> {

    private static final long serialVersionUID = 216418332L;

    public static final QFileData fileData = new QFileData("fileData");

    public final ArrayPath<byte[], Byte> data = createArray("data", byte[].class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public QFileData(String variable) {
        super(FileData.class, forVariable(variable));
    }

    public QFileData(Path<? extends FileData> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileData(PathMetadata metadata) {
        super(FileData.class, metadata);
    }

}

