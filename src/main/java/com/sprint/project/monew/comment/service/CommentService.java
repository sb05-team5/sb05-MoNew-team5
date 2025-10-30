package com.sprint.project.monew.comment.service;

import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.article.repository.ArticleRepository;
import com.sprint.project.monew.comment.dto.CommentDto;
import com.sprint.project.monew.comment.entity.Comment;
import com.sprint.project.monew.comment.mapper.CommentMapper;
import com.sprint.project.monew.comment.repository.CommentRepository;
import com.sprint.project.monew.common.CursorPageResponse;
import com.sprint.project.monew.user.entity.User;
import com.sprint.project.monew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

//    private final CommentRepository commentRepository;
//    private final CommentMapper commentMapper;
//    private final UserRepository userRepository;
//    private final ArticleRepository articleRepository;
//
//    private static final Pattern CURSOR_PATTERN =
//            Pattern.compile("^(date|likes):([^#]+)#([0-9a-fA-F\\-]{36})$");
//
//    @Transactional(readOnly = true)
//    public CursorPageResponse<CommentDto> pageByArticle(
//            UUID articleId,
//            String sort,
//            String order,
//            String cursor,
//            int size
//    ) {
//        boolean byDate = "date".equalsIgnoreCase(sort);
//        boolean asc    = "asc".equalsIgnoreCase(order);
//
//        Instant dateCursor = null;
//        Integer likeCursor = null;
//        UUID idCursor      = null;
//
//        if (cursor != null && !cursor.isBlank()) {
//            var m = CURSOR_PATTERN.matcher(cursor);
//            if (!m.matches()) {
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 커서 형식입니다.");
//            }
//            String type = m.group(1);
//            String primary = m.group(2);
//            idCursor = UUID.fromString(m.group(3));
//
//            if ("date".equals(type)) {
//                dateCursor = Instant.ofEpochMilli(Long.parseLong(primary));
//            } else {
//                likeCursor = Integer.parseInt(primary);
//            }
//        }
//
//        List<Comment> rows = byDate
//                ? commentRepository.findOrderByDate(articleId, asc, dateCursor, idCursor, size + 1)
//                : commentRepository.findOrderByLikes(articleId, asc, likeCursor, idCursor, size + 1);
//
//        boolean hasNext = rows.size() > size;
//        if (hasNext) rows = rows.subList(0, size);
//
//        List<CommentDto> content = rows.stream()
//                .map(commentMapper::toDto)
//                .toList();
//
//        String nextCursor = null;
//        if (hasNext && !rows.isEmpty()) {
//            Comment last = rows.get(rows.size() - 1);
//            nextCursor = byDate
//                    ? "date:"  + last.getCreatedAt().toEpochMilli() + "#" + last.getId()
//                    : "likes:" + last.getLikeCount()                 + "#" + last.getId();
//        }
//
//        return CursorPageResponse.<CommentDto>builder()
//                .content(content)
//                .nextCursor(nextCursor)
//                .nextAfter(null)
//                .size(content.size())
//                .hasNext(hasNext)
//                .totalElements(null)
//                .build();
//    }
//
//    @Transactional
//    public UUID create(UUID articleId, UUID userId, String content) {
//
//        Article articleRef = articleRepository.getReferenceById(articleId);
//        User userRef = userRepository.getReferenceById(userId);
//
//        Comment saved = commentRepository.save(Comment.create(articleRef, userRef, content));
//        return saved.getId();
//    }
//
//    @Transactional
//    public void update(UUID commentId, UUID userId, String content) {
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."));
//
//        if (!comment.getUser().getId().equals(userId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 수정할 수 있습니다.");
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
//        if (!comment.getUser().getId().equals(userId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 삭제할 수 있습니다.");
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
//        if (!comment.getUser().getId().equals(userId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "작성자만 삭제할 수 있습니다.");
//        }
//        commentRepository.delete(comment);
//    }
}
