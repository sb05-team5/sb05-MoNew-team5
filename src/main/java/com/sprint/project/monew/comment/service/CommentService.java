package com.sprint.project.monew.comment.service;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.article.service.ArticleService;
import com.sprint.project.monew.comment.dto.CommentDto;
import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.mapper.CommentMapper;
import com.sprint.project.monew.comment.repository.CommentRepository;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ArticleService articleService;

    private static final Pattern CURSOR_PATTERN =
            Pattern.compile("^(date|likes):([^#]+)#([0-9a-fA-F\\-]{36})$");


    public UUID getArticleId(UUID commentId) {
        return commentRepository.findArticleId(commentId);
    }


    @Transactional(readOnly = true)
    public CursorPageResponse<CommentDto> pageByArticle(
            UUID articleId,
            String sort,
            String order,
            String cursor,
            int size
    ) {
        boolean byDate = "date".equalsIgnoreCase(sort);
        boolean asc    = "asc".equalsIgnoreCase(order);

        Instant dateCursor = null;
        Integer likeCursor = null;
        UUID idCursor      = null;

        if (cursor != null && !cursor.isBlank()) {
            var m = CURSOR_PATTERN.matcher(cursor);
            if (!m.matches()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 커서 형식입니다.");
            }

            String type = m.group(1);
            String primary = m.group(2);
            idCursor = UUID.fromString(m.group(3));

            if ("date".equals(type)) {
                dateCursor = Instant.ofEpochMilli(Long.parseLong(primary));
            } else {
                likeCursor = Integer.parseInt(primary);
            }
        }

        String after = null;
        if (idCursor != null) {
            if (byDate && dateCursor != null) {
                after = dateCursor.toEpochMilli() + "|" + idCursor;
            } else if (!byDate && likeCursor != null) {
                after = likeCursor + "|" + idCursor;
            }
        }

        String orderBy = byDate ? "date" : "likes";
        String direction = asc ? "asc" : "desc";

        CursorPageResponse<Comment> commentPage =
                commentRepository.pageByArticleSorted(articleId, orderBy, direction, after, size);

        List<CommentDto> content = commentPage.content().stream()
                .map(commentMapper::toDto)
                .toList();

        return new CursorPageResponse<>(
                content,
                commentPage.nextCursor(),
                toExternalCursor(commentPage.nextAfter(), byDate),
                commentPage.size(),
                commentPage.hasNext(),
                commentPage.totalElements()
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

        Article articleRef = articleRepository.getReferenceById(articleId);
        User userRef = userRepository.getReferenceById(userId);

        Comment saved = commentRepository.save(Comment.create(articleRef, userRef, content));

        eventPublisher.publishEvent(new CommentRegisterEvent(saved, articleRef, userRef));

        return saved.getId();
    }

    @Transactional
    public void update(UUID commentId, UUID userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 수정할 수 있습니다.");
        }

        comment.update(content);

        eventPublisher.publishEvent(new CommentUpdateEvent(this, comment));
    }

    @Transactional
    public void softDelete(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));

        UUID articleId = commentRepository.findArticleId(commentId);
        articleService.decremontCommentCount(articleId);

        eventPublisher.publishEvent(new CommentDeleteEvent(this, comment));


        comment.softDelete();
    }

    @Transactional
    public void hardDelete(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));

        eventPublisher.publishEvent(new CommentDeleteEvent(this, comment));

        commentRepository.delete(comment);
    }
}
