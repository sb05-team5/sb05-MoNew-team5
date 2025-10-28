package com.sprint.project.monew.articleView.mapper;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReferenceMapper {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public Article toArticle(UUID id) {
        if (id == null) return null;
        return articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found: " + id));
    }

    public User toUser(UUID id) {
        if (id == null) return null;
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }
}



