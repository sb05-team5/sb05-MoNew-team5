package com.sprint.project.monew.commentLike.service;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.repository.CommentRepository;
import com.sprint.project.monew.commentLike.dto.CommentLikeDto;
import com.sprint.project.monew.commentLike.entity.CommentLike;
import com.sprint.project.monew.commentLike.mapper.CommentLikeMapper;
import com.sprint.project.monew.commentLike.repository.CommentLikeRepository;
import com.sprint.project.monew.log.event.CommentLikeDeleteEvent;
import com.sprint.project.monew.notification.service.NotificationService;
import com.sprint.project.monew.log.event.CommentLikeRegisterEvent;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final CommentLikeMapper commentLikeMapper;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CommentLikeDto commentLike(UUID commentId, UUID userId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
        User user = userRepository.getReferenceById(userId);

        CommentLike like = commentLikeRepository.findByComment_IdAndUser_Id(commentId, userId)
                .orElseGet(() -> commentLikeRepository.save(CommentLike.create(comment, user)));

        long likeCount = commentLikeRepository.countByComment_Id(commentId);

        Article article = articleRepository.findById(comment.getArticle().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "기사가 존재하지 않습니다."));

        String content = comment.getContent();

        eventPublisher.publishEvent(new CommentLikeRegisterEvent(article, comment, content, user));

        return commentLikeMapper.toDto(like, comment, likeCount);
    }

    @Transactional
    public void uncommentLike(UUID commentId, UUID userId) {
        CommentLike commentLike = commentLikeRepository.findByComment_IdAndUser_Id(
                commentId, userId).orElseThrow(() -> new NoSuchElementException("댓글 x"));

        eventPublisher.publishEvent(new CommentLikeDeleteEvent(this, commentLike));

        commentLikeRepository.deleteByComment_IdAndUser_Id(commentId, userId);
    }
}
