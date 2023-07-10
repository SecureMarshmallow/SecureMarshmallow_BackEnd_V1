package com.junha.securemarshmallow.controller;

import com.junha.securemarshmallow.ServerMessage;
import com.junha.securemarshmallow.domain.constant.SearchType;
import com.junha.securemarshmallow.dto.ArticleDto;
import com.junha.securemarshmallow.dto.request.ArticleRequest;
import com.junha.securemarshmallow.dto.response.ArticleResponse;
import com.junha.securemarshmallow.service.ArticleService;
import com.junha.securemarshmallow.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Size;
@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;

    @GetMapping
    public ResponseEntity<?> articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) @Size(max = 255, min = 1) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap map
    ) {
        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "AccessToken generate Failed."));
    }

    @PostMapping("/form")
    public ResponseEntity<?> postNewArticle(@RequestBody @Valid ArticleDto articleDto, BindingResult bindingResult)
    {
        try {
            System.out.println("Received ArticleDto:");
            System.out.println("Title: " + articleDto.title());
            System.out.println("Content: " + articleDto.content());
            System.out.println("Created At: " + articleDto.createdAt());
            System.out.println("Created By: " + articleDto.createdBy());
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error","Failed to Save.."));
            }
            articleService.saveArticle(articleDto);
            return ResponseEntity.status(HttpStatus.OK).body(new ServerMessage("success","Save Successfully."));
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error","Failed to Save!"));
        }

    }

    @PostMapping("/{articleId}/form")
    public ResponseEntity<?> updateArticle(@PathVariable Long articleId, @Valid ArticleDto articleDto, BindingResult bindingResult)
    {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ServerMessage("error", "Failed to Update."));
        }

        articleService.updateArticle(articleId, articleDto);

        return ResponseEntity.ok(Map.of("success","Updated"));
    }

    @PostMapping("/{articleId}")
    public ResponseEntity<?> viewArticle(@PathVariable Long articleId) {
        try {
            ArticleDto articleDto = articleService.getArticle(articleId);
            if (articleDto != null) {
                return ResponseEntity.ok(articleDto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ServerMessage("error", "Article not found."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ServerMessage("error", "Failed to retrieve article."));
        }
    }




    @PostMapping("/{articleId}/delete")
    public ResponseEntity<?> deleteArticle(@PathVariable Long articleId, String UserId) {
        try {
            articleService.deleteArticle(articleId, UserId);
            return ResponseEntity.status(HttpStatus.OK).body(new ServerMessage("success", "Delete Successfully."));
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServerMessage("error", "Delete Failed."));
        }
    }

}