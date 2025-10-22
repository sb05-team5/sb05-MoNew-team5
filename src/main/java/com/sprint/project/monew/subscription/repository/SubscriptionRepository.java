package com.sprint.project.monew.subscription.repository;

import com.sprint.project.monew.subscription.entity.Subscription;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

}
