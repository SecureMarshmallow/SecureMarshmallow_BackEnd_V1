package com.junha.securemarshmallow.entity;

import com.junha.securemarshmallow.domain.FileDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file")
@Getter
@Setter
@NoArgsConstructor //매개변수가 없는
@AllArgsConstructor //모든 필드의 매개변수가 있는
public class File {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private long fileSize;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String hashtag;

    public FileDto toDto() {
        return new FileDto(id, fileName, fileSize, createAt,createdBy,hashtag);
    }
}