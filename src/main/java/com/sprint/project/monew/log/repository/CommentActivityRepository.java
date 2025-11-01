package com.sprint.project.monew.log.repository;

import com.sprint.project.monew.log.document.CommentActivity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentActivityRepository extends MongoRepository<CommentActivity,String> {

  List<CommentActivity> findTop10ByUserIdOrderByCreatedAtDesc(String userId);
}
