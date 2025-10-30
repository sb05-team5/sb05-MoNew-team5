package com.sprint.project.monew.log.document;

import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.user.entity.User;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscriptions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class SubscriptionActivity {

  private UUID id;
  private Instant createdAt;
  private Interest interest;
  private User user; //보고 보류

}
