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

@Document("comment_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class CommentLikeActivity {

  @Id
  private String id = UUID.randomUUID().toString();

  @Field("created_at")
  private Instant createdAt;

  @Field("article_id")
  private String articleId;
  private String articleTitle;

  @Field("comment_id")
  private String commentId;
  private String commentContent;
  private int commentLikeCount;
  private Instant commentCreatedAt;

  @Field("user_id")
  private String userId;
  private String userName;







}
