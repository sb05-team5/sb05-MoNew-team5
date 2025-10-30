package com.sprint.project.monew.log.repository;

import com.sprint.project.monew.log.document.SubscriptionActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubscriptionActivityRepository extends MongoRepository<SubscriptionActivity,String> {
    List<SubscriptionActivity> findAllByOrderByCreatedAtDesc();
}
