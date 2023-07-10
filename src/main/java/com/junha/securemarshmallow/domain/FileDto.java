package com.junha.securemarshmallow.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private UUID id;
    private String fileName;
    private long fileSize;
    private LocalDateTime createAt;
    private String createdBy;
    private String hashtag;
}