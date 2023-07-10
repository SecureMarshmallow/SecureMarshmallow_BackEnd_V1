package com.junha.securemarshmallow.service;

import com.junha.securemarshmallow.ServerMessage;
import com.junha.securemarshmallow.domain.Article;
import com.junha.securemarshmallow.domain.UserAccount;
import com.junha.securemarshmallow.domain.constant.SearchType;
import com.junha.securemarshmallow.dto.ArticleDto;
import com.junha.securemarshmallow.repository.ArticleRepository;
import com.junha.securemarshmallow.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("No Memo. - articleId: " + articleId));
    }

    public ResponseEntity<ServerMessage> saveArticle(ArticleDto articleDto) {
        try {
            UserAccount userAccount = getUserAccount(articleDto.createdBy());
            articleRepository.save(articleDto.toEntity(articleDto));
            return ResponseEntity.status(HttpStatus.OK).body(new ServerMessage("success", "Save Successfully."));
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "Failed to save."));
        }
    }

    public void updateArticle(Long articleId, ArticleDto dto) {
        try {
            Article article = getArticleById(articleId);
            UserAccount userAccount = getUserAccount(dto.createdBy());

            if (article.getCreatedBy().equals(userAccount.getUserId())) {
                if (dto.title() != null) {
                    article.setTitle(dto.title());
                }
                if (dto.content() != null) {
                    article.setContent(dto.content());
                }
                if (dto.hashtag() != null) {
                    article.setHashtag(dto.hashtag());
                }
                article.setModifiedAt(LocalDateTime.now());
            } else {
                throw new EntityNotFoundException("None Memo. - articleId: " + articleId);
            }
        } catch (EntityNotFoundException e) {
            log.warn("Failed to Update.", e.getLocalizedMessage());
            throw e;
        }
    }

    public void deleteArticle(long articleId, String user_id) {
        Optional<Article> articleOptional = articleRepository.findById(articleId);
        if (articleOptional.isPresent()) {
            Article article = articleOptional.get();
            if (article.getCreatedBy().equals(user_id)) {
                articleRepository.deleteByIdAndCreatedBy(articleId, user_id);
            } else {
                throw new EntityNotFoundException("게시글이 없거나 사용자와 일치하지 않습니다 - articleId: " + articleId);
            }
        } else {
            throw new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId);
        }
    }

    public long getArticleCount() {
        return articleRepository.count();
    }

    private UserAccount getUserAccount(String user_id) {
        return userAccountRepository.findById(user_id)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다 - userId: " + user_id));
    }

    private Article getArticleById(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }
}
