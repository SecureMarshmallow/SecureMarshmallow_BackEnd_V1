package com.junha.securemarshmallow.dto.request;

import com.junha.securemarshmallow.dto.ArticleDto;
import com.junha.securemarshmallow.dto.UserAccountDto;

import javax.validation.constraints.Size;

public record ArticleRequest(
        @Size(max = 255, min = 1) String title,
        @Size(max = 3000, min = 1) String content
) {

    public static ArticleRequest of(String title, String content) {
        return new ArticleRequest(title, content);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto,ArticleDto articleDto) {
        return ArticleDto.of(
                userAccountDto,
                articleDto
        );
    }

}//Complete