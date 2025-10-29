package com.sprint.project.monew.interest.repository;

import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.interest.entity.Subscription;
import com.sprint.project.monew.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

  boolean existsByUserAndInterest(User user, Interest interest);
  void deleteByUserAndInterest(User user, Interest interest);
  boolean existsByInterestId(UUID interestId);
}
