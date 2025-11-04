package com.sprint.project.monew.commentLike.service;

import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.repository.CommentRepository;
import com.sprint.project.monew.commentLike.dto.CommentLikeDto;
import com.sprint.project.monew.commentLike.entity.CommentLike;
import com.sprint.project.monew.commentLike.mapper.CommentLikeMapper;
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

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentLikeMapper commentLikeMapper;

    @Transactional(readOnly = true)
    public int getLikeCount(UUID commentId) {
        return commentLikeRepository.countByComment_Id(commentId);
    }

    @Transactional(readOnly = true)
    public boolean likedByMe(UUID commentId, UUID userId) {
        if (userId == null) {
            return false;
        }
        return commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId);
    }

    @Transactional
    public CommentLikeDto commentLike(UUID commentId, UUID userId) {

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId가 필요합니다.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
        User user = userRepository.getReferenceById(userId);

        CommentLike like = commentLikeRepository.findByComment_IdAndUser_Id(commentId, userId)
                .orElseGet(() -> commentLikeRepository.save(CommentLike.create(comment, user)));

        long newCount = commentLikeRepository.countByComment_Id(commentId);
        return commentLikeMapper.toDto(like, comment, newCount);
    }

    @Transactional
    public void uncommentLike(UUID commentId, UUID userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId가 필요합니다.");
        }
        commentLikeRepository.deleteByComment_IdAndUser_Id(commentId, userId);
    }
}
