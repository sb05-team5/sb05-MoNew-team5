package com.sprint.project.monew.log.document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user_activities")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserActivity {

  private UUID id;
  private List<SubscriptionActivity> subscriptions;
  private List<ArticleViewActivity> articleViews;
  private Instant createdAt;




}
