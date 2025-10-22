package com.sprint.project.monew.comment.repository;

import com.sprint.project.monew.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    List<CommentEntity> findByArticleId(UUID articleId);
}
