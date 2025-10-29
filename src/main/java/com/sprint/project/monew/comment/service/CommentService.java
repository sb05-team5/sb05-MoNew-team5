package com.sprint.project.monew.comment.service;

import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

//    private final CommentRepository commentRepository;
//
//    @Transactional
//    public UUID create(UUID articleId, UUID userId, String content) {
//
//        if (articleId == null || userId == null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 접근입니다.");
//        }
//
//        if (content == null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용을 입력해야 합니다.");
//        }
//
//        Comment saved = commentRepository.save(Comment.create(userId, articleId, content));
//        return saved.getId();
//    }
//
//    @Transactional
//    public void update(UUID commentId, UUID userId, String content) {
//        Comment comment = commentRepository.findByCommentId(commentId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
//
//        if (!comment.getUserId().equals(userId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 수정할 수 있습니다.");
//        }
//
//        if (comment.getDeletedAt() != null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 댓글입니다.");
//        }
//
//        comment.update(content);
//    }
//
//    @Transactional
//    public void softDelete(UUID commentId, UUID userId) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
//
//        if (!comment.getUserId().equals(userId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 삭제할 수 있습니다.");
//        }
//
//        if (comment.getDeletedAt() != null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 댓글입니다.");
//        }
//
//        comment.softDelete();
//    }
//
//    @Transactional
//    public void hardDelete(UUID commentId, UUID userId) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
//
//        if (!comment.getUserId().equals(userId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 삭제할 수 있습니다.");
//        }
//        commentRepository.delete(comment);
//    }
}
