package com.sprint.project.monew.log.document;

import com.querydsl.core.annotations.QueryEntity;
import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.user.entity.User;
import java.time.Instant;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@QueryEntity
@Document("article_views")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class ArticleViewActivity {

  @Id
  private String id = UUID.randomUUID().toString();

  private Instant createdAt;

  @Field("user_id")
  private UUID viewedBy;

  private UUID articleId;
  private String source;
  private String sourceUrl;
  private String articleTitle;
  private String articlePublishedDate;
  private String articleSummary;
  private Long articleCommentCount;
  private Integer articleViewCount;
}
