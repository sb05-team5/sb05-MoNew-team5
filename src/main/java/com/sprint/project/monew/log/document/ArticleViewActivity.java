package com.sprint.project.monew.log.document;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.user.entity.User;
import java.time.Instant;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("article_views")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class ArticleViewActivity {

  @Id
  private String id = UUID.randomUUID().toString();

  @Field("created_at")
  private Instant createdAt;

  private String viewedBy;

  @Field("article_Id")
  private String articleId;

  private String source;

  private String sourceUrl;

  private String articleTitle;

  private String articlePublishedDate;

  private String articleSummary;

  private Integer articleCommentCount;

  private Integer articleViewCount;

  public ArticleViewActivity update(Integer commentCount) {
    return this.toBuilder()
            .articleCommentCount(commentCount)
            .build();
  }

  public ArticleViewActivity updateView(Integer viewCount) {
    return this.toBuilder()
            .articleViewCount(viewCount)
            .build();
  }

}
