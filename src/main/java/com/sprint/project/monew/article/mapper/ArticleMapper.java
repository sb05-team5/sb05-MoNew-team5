package com.sprint.project.monew.article.mapper;

import com.sprint.project.monew.article.dto.ArticleDto;
import com.sprint.project.monew.article.dto.ArticleDtoUUID;
import com.sprint.project.monew.article.entity.Article;
import jakarta.persistence.Column;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ArticleMapper {



    @Named("stringToUUID")
    default UUID stringToUUID(String id) {
        return id != null ? UUID.fromString(id) : null;
    }

    // ✅ UUID → String 변환
    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }

    @Mapping(target = "id", source = "id", qualifiedByName = "stringToUUID")
    @Mapping(target = "interest_id", ignore = true)
    Article toEntity(ArticleDto articleDto);


    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "viewedByBme", ignore = true)
    @Mapping(target = "publishDate", source = "publishDate", qualifiedByName = "instantToString")
    ArticleDto toDto(Article article);


    @Mapping(target = "id", source = "id", qualifiedByName = "uuidToString")
    ArticleDto toDto(ArticleDtoUUID articleDtoUUID);


    @Named("instantToString")
    default String instantToString(Instant instant) {
        if (instant == null) return null;
        return DateTimeFormatter.ISO_INSTANT.format(instant);
        //DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul")).format(instant)
    }
}
