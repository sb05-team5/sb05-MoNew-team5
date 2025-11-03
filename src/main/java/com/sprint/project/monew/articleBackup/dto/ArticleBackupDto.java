package com.sprint.project.monew.articleBackup.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ArticleBackupDto {

    private UUID id;

    private Instant createdAt;

    private UUID article_id;

    private String publishDate;

    private String source;

    private String sourceUrl;

    private String title;

    private String summary;

    private int commentCount;

    private int viewCount;

    private UUID interest_id;

    private Instant deleted_at;

}
