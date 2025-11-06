package com.sprint.project.monew.log.repository;

import com.sprint.project.monew.log.document.CommentActivity;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentActivityRepository extends MongoRepository<CommentActivity,String> {

  List<CommentActivity> findTop10ByUserIdOrderByCreatedAtDesc(String userId);
  void deleteById(String id);
}
