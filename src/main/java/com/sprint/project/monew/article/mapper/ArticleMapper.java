package com.sprint.project.monew.article.mapper;

import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.entity.Article;
import jakarta.persistence.Column;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ArticleMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    @Mapping(target = "interest_id", ignore = true)
    Article toEntity(ArticleDto articleDto);


    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "viewedByBme", ignore = true)
    @Mapping(target = "publishDate", source = "publishDate")
    ArticleDto toDto(Article article);
}
