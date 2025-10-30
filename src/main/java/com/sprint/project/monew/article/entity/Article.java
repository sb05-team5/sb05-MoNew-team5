package com.sprint.project.monew.article.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.project.monew.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "articles")
@Getter
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@AllArgsConstructor
@NoArgsConstructor
public class Article extends BaseEntity {


    @Column(nullable = false)
    private String publishDate;

    @Column(nullable = false, length = 255)
    private String source;

    @Column(nullable = false, length = 255, unique = true)
    private String sourceUrl;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private String summary;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(nullable = true, length = 255)
    private UUID interest_id;

    @Column(nullable = true)
    private Instant deleted_at;



}
