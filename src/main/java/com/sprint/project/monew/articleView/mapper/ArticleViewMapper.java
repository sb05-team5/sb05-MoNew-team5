package com.sprint.project.monew.articleView.mapper;

import com.sprint.project.monew.articleView.dto.ArticleViewDto;
import com.sprint.project.monew.articleView.entity.ArticleView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ReferenceMapper.class})
public interface ArticleViewMapper {


    @Mapping(target = "article", source = "articleId" )
    @Mapping(target = "user", source = "viewedBy" )
    ArticleView toEntity(ArticleViewDto articleViewDto);


// 추후에 관련된 코드가 갖춰지면 완성시키기 ( 댓글, 좋아요 , 관심사, 키워드)
//    @Mapping(target = "viewedBy", source = "user.id")
//    @Mapping(target = "articleId", source = "article.id")
//    @Mapping(target = "source", source = "article.source")
//    @Mapping(target = "sourceUrl", source = "article.sourceUrl")
//    @Mapping(target = "articleTitle", source = "article.title")
//    @Mapping(target = "articlePublishedDate", source = "article.publishDate")
//    @Mapping(target = "articleSummary", source = "article.summary")
//    @Mapping(target = "articleViewCount", source = "article.viewCount")
//    //@Mapping(target = "articleCommentCount", source = "article.commentCount")  // Todo
//    ArticleViewDto toDto(ArticleView articleView);
}
