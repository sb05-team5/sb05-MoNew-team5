package com.sprint.project.monew.log.repository;

import com.sprint.project.monew.log.document.CommentActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentActivityRepository extends MongoRepository<CommentActivity,String> {

}
