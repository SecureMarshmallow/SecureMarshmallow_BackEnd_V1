package com.junha.securemarshmallow.dto;
import com.junha.securemarshmallow.domain.Article;
import com.junha.securemarshmallow.domain.UserAccount;
import java.time.LocalDateTime;
public record ArticleDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy,
        String hashtag
) {
    public static ArticleDto of(UserAccountDto userAccountDto, ArticleDto articleDto) {
        return new ArticleDto(null, articleDto.title(), articleDto.content(), articleDto.createdAt(), userAccountDto.user_id(), null, null, articleDto.hashtag());
    }

    public static ArticleDto of(Long id, UserAccountDto userAccountDto, String title, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy, String hashtag) {
        return new ArticleDto(id, title, content, createdAt, createdBy, modifiedAt, modifiedBy, hashtag);
    }
    //Complete
    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy(),
                entity.getHashtag()
        );
    }

    public Article toEntity(ArticleDto articleDto) {
        return Article.of(
                articleDto
        );
    }
}
