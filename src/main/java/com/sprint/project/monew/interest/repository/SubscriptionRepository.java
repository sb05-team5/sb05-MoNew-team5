package com.sprint.project.monew.interest.repository;

import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.entity.Subscription;
import com.sprint.project.monew.user.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

  boolean existsByUserAndInterest(User user, Interest interest);
  void deleteByUserAndInterest(User user, Interest interest);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete Subscription s where s.user.id in :userIds")
  void deleteAllByUserIds(List<UUID> userIds);
}
