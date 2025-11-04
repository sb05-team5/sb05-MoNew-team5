package com.sprint.project.monew.log.document;

import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class CommentActivity {

  @Id
  private String id;

  @Field("created_at")
  private Instant createdAt;

  @Field("article_id")
  private String articleId;
  private String articleTitle;

  @Field("user_id")
  private String userId;
  private String userName;

  private String content;
  private int likeCount;

  public CommentActivity update(String newContent) {
    return this.toBuilder()
        .content(newContent)
        .build();
  }



}
