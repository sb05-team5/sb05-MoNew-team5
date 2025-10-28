package com.sprint.project.monew.articleView.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sprint.project.monew.article.entity.Article;
import com.sprint.project.monew.common.BaseEntity;
import com.sprint.project.monew.interest.entity.Interest;
import com.sprint.project.monew.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "article_views")
@Getter
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@AllArgsConstructor
@NoArgsConstructor
public class ArticleView extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


}
