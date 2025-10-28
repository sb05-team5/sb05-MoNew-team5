package com.sprint.project.monew.articleBackup.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "article_backup")
@Getter
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@AllArgsConstructor
@NoArgsConstructor
public class ArticleBackup extends BaseEntity {

    @Column(nullable = false)
    private UUID article_id;

    @Column(nullable = false)
    private Instant publishDate;

    @Column(nullable = false, length = 255)
    private String source;

    @Column(nullable = false, length = 255, unique = true)
    private String sourceUrl;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = true, length = 255)
    private UUID interest_id;

    @Column(nullable = true)
    private Instant deleted_at;

}
