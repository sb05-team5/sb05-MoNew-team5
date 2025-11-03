package com.sprint.project.monew.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.project.monew.article.mapper.ArticleMapper;
import com.sprint.project.monew.article.mapper.ArticleMapperImpl;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestRepositoryConfig {

  @Bean
  public JPAQueryFactory jpaQueryFactory(EntityManager em) {
    return new JPAQueryFactory(em);
  }

  @Bean
  public ArticleMapper articleMapper(EntityManager em) {
    return new ArticleMapperImpl();
  }
}
