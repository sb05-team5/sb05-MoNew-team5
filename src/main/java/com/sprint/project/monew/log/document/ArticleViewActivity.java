package com.sprint.project.monew.log.document;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.user.entity.User;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("article_views")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ArticleViewActivity {

  private UUID id;
  private Instant createdAt;
  private User user;
  private Article article;

}
