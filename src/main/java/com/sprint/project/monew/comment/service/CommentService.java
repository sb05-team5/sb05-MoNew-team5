package com.sprint.project.monew.comment.service;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.article.service.ArticleService;
import com.sprint.project.monew.comment.dto.CommentDto;
import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.mapper.CommentMapper;
import com.sprint.project.monew.comment.repository.CommentRepository;
import com.sprint.project.monew.commentLike.repository.CommentLikeRepository;
import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.log.event.CommentDeleteEvent;
import com.sprint.project.monew.log.event.CommentRegisterEvent;
import com.sprint.project.monew.log.event.CommentUpdateEvent;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public CursorPageResponse<CommentDto> pageByArticle(
            UUID articleId,
            String orderBy,
            String direction,
            String cursorOrAfter,
            int limit,
            UUID requestUserId
    ) {
        CursorPageResponse<Comment> page = commentRepository.pageByArticleSorted(
                articleId, orderBy, direction, cursorOrAfter, limit
        );

        List<Comment> rows = page.content();
        if (rows.isEmpty()) {
            return new CursorPageResponse<>(
                    List.of(), null, page.nextAfter(), 0, page.hasNext(), page.totalElements()
            );
        }

        List<UUID> ids = rows.stream().map(Comment::getId).toList();

        Map<UUID, Long> likeCountMap = commentLikeRepository.countGroupByCommentIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        arr -> (UUID) arr[0],
                        arr -> (Long) arr[1]
                ));

        Set<UUID> likedByMeIds = (requestUserId == null)
                ? Collections.emptySet()
                : commentLikeRepository.findLikedCommentIds(requestUserId, ids);

        List<CommentDto> content = rows.stream()
                .map(c -> commentMapper.toDtoWithCountsAndLiked(
                        c,
                        likeCountMap.getOrDefault(c.getId(), 0L),
                        likedByMeIds.contains(c.getId())
                ))
                .toList();

        return new CursorPageResponse<>(
                content,
                null,
                page.nextAfter(),
                content.size(),
                page.hasNext(),
                page.totalElements()
        );
    }

    private String toExternalCursor(String nextAfter, boolean byDate) {
        if (nextAfter == null || nextAfter.isBlank()) {
            return null;
        }

        String[] parts = nextAfter.split("\\|", 2);

        if (parts.length != 2) {
            return null;
        }

        return (byDate ? "date:" : "likes:") + parts[0] + "#" + parts[1];
    }

    @Transactional
    public UUID create(UUID articleId, UUID userId, String content) {

        if (articleId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ArticleId가 필요합니다.");
        }
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserId가 필요합니다.");
        }

        Article articleRef = articleRepository.getReferenceById(articleId);
        User userRef = userRepository.getReferenceById(userId);

        Comment saved = commentRepository.save(Comment.create(articleRef, userRef, content));

        eventPublisher.publishEvent(new CommentRegisterEvent(saved, articleRef, userRef));

        return saved.getId();
    }

    @Transactional
    public CommentDto update(UUID commentId, UUID userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 수정할 수 있습니다.");
        }

        comment.update(content);

        long likeCount = commentLikeRepository.countByComment_Id(comment.getId());
        return commentMapper.toDtoWithCountsAndLiked(comment, likeCount, false);
    }

    @Transactional
    public void softDelete(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
        comment.softDelete();
    }

    @Transactional
    public void hardDelete(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));

        eventPublisher.publishEvent(new CommentDeleteEvent(this, comment));

        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public CommentDto findDtoById(UUID id) {
        Comment c = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
        long likeCount = commentLikeRepository.countByComment_Id(c.getId());
        return commentMapper.toDtoWithCounts(c, likeCount);
    }

    public UUID getArticleId(UUID commentID) {
        return commentRepository.findArticleId(commentID);
    }
}
