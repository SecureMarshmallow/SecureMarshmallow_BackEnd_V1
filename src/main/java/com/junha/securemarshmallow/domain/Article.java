package com.junha.securemarshmallow.domain;

import com.junha.securemarshmallow.dto.ArticleDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
@Getter
@Entity
public class Article extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false, length = 10000)
    private String content;

    @Setter
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Setter
    @Column(nullable = false, length = 100)
    private String createdBy;

    @Setter
    @Column(nullable = false, length = 255)
    private String hashtag;

    @Setter
    @Column(nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime modifiedAt;


    protected Article() {
    }

    private Article(ArticleDto articleDto) {
        this.title = articleDto.title();
        this.content = articleDto.content();
        this.createdAt = articleDto.createdAt();
        this.createdBy = articleDto.createdBy();
        this.hashtag = articleDto.hashtag();
    }

    public static Article of(ArticleDto articleDto) {
        return new Article(articleDto);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}