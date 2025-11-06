package com.sprint.project.monew.log.repository;

import com.sprint.project.monew.log.document.CommentLikeActivity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentLikeActivityRepository extends MongoRepository<CommentLikeActivity,String> {

  List<CommentLikeActivity> findTop10ByUserIdOrderByCreatedAtDesc(String userId);
  void deleteByCommentIdAndUserId(String commentId, String userId);

  List<CommentLikeActivity> findAllByCommentId(String commentId);
  void deleteAllByCommentId(String commentId);
}
