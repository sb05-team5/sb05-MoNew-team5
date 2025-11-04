package com.sprint.project.monew.comment.service;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.comment.dto.CommentDto;
import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.mapper.CommentMapper;
import com.sprint.project.monew.comment.repository.CommentRepository;
import com.sprint.project.monew.commentLike.repository.CommentLikeRepository;
import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional(readOnly = true)
    public CursorPageResponse<CommentDto> pageByArticle(
            UUID articleId,
            String orderBy,
            String direction,
            String cursorOrAfter,
            int limit
    ) {
        String after = normalizeAfter(cursorOrAfter);

        var page = commentRepository.pageByArticleSorted(
                articleId, orderBy, direction, after, limit
        );

        List<Comment> entities = page.content();
        if (entities.isEmpty()) {
            return new CursorPageResponse<>(
                    List.of(), null, null, 0, false, page.totalElements()
            );
        }

        List<UUID> ids = entities.stream().map(Comment::getId).toList();
        Map<UUID, Long> likeCountMap = commentLikeRepository.countByCommentIdsRaw(ids).stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> (Long) row[1]
                ));

        List<CommentDto> content = entities.stream()
                .map(c -> commentMapper.toDtoWithCounts(c, likeCountMap.getOrDefault(c.getId(), 0L) ))
                .toList();

        return new CursorPageResponse<>(
                content,
                null,
                page.nextAfter(),
                page.size(),
                page.hasNext(),
                page.totalElements()
        );
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
        return commentMapper.toDtoWithCounts(comment, likeCount);
    }

    @Transactional
    public void softDelete(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 삭제할 수 있습니다.");
        }
        comment.softDelete();
    }

    @Transactional
    public void hardDelete(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public CommentDto findDtoById(UUID id) {
        Comment c = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
        long likeCount = commentLikeRepository.countByComment_Id(c.getId());
        return commentMapper.toDtoWithCounts(c, likeCount);
    }

    private String normalizeAfter(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        return s.isEmpty() ? null : s;
    }

    public UUID getArticleId(UUID commentID) {
        return commentRepository.findArticleId(commentID);
    }
}
