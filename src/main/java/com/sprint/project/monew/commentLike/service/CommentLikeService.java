package com.sprint.project.monew.commentLike.service;

import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.repository.CommentRepository;
import com.sprint.project.monew.commentLike.entity.CommentLike;
import com.sprint.project.monew.commentLike.repository.CommentLikeRepository;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

//    private final CommentLikeRepository commentLikeRepository;
//    private final CommentRepository commentRepository;
//    private final UserRepository userRepository;
//
//    @Transactional
//    public int commentLike(UUID commentId,UUID userId) {
//
//        if (commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId)) {
//            return commentLikeRepository.countByComment_Id(commentId);
//        }
//
//        Comment comment = commentRepository.findForUpdate(commentId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
//
//        if (comment.getDeletedAt() != null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 댓글입니다.");
//        }
//
//        User userRef = userRepository.getReferenceById(userId);
//
//        commentLikeRepository.save(CommentLike.create(comment, userRef));
//
//        comment.increaseLike();
//
//        return comment.getLikeCount();
//    }
//
//    @Transactional
//    public int uncommentLike(UUID commentId, UUID userId) {
//        if (!commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId)) {
//            return commentLikeRepository.countByComment_Id(commentId);
//        }
//
//        Comment comment = commentRepository.findForUpdate(commentId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
//        if (comment.getDeletedAt() != null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 댓글입니다.");
//        }
//
//        commentLikeRepository.deleteByComment_IdAndUser_Id(commentId, userId);
//        comment.decreaseLike();
//
//        return comment.getLikeCount();
//    }
}
