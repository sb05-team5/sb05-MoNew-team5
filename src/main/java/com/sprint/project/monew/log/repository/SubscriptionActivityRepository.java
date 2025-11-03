package com.sprint.project.monew.log.repository;

import com.sprint.project.monew.log.document.SubscriptionActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


public interface SubscriptionActivityRepository extends MongoRepository<SubscriptionActivity,String> {
    List<SubscriptionActivity> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);
}
