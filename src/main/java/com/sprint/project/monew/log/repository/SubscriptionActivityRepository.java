package com.sprint.project.monew.log.repository;

import com.sprint.project.monew.log.document.SubscriptionActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionActivityRepository extends MongoRepository<SubscriptionActivity,String> {
    List<SubscriptionActivity> findAllUserIdByOrderByCreatedAtDesc(String userId);

    void deleteByInterestId(String interestId);
    void deleteByInterestIdAndUserId(String interestId, String userId);
}
