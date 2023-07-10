package com.junha.securemarshmallow.dto.response;

import com.junha.securemarshmallow.dto.ArticleDto;

import java.time.LocalDateTime;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        String nickname
) {

    public static ArticleResponse of(Long id, String title, String content, LocalDateTime createdAt, String nickname) {
        return new ArticleResponse(id, title, content, createdAt, nickname);
    }

    public static ArticleResponse from(ArticleDto dto) {
        String nickname = dto.createdBy();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.createdBy();
        }

        return new ArticleResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.createdAt(),
                nickname
        );
    }

}
//Complete