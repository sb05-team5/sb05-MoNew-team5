package com.sprint.project.monew.articleView.repository;

import java.util.UUID;

public interface ArticleViewQueryRepository {

    boolean existsById(UUID articleId, UUID userId);
}
