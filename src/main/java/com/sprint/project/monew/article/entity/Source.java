package com.sprint.project.monew.article.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Source {
    NAVER("NAVER"),
    HANKYUNG("HANKYUNG"),
    CHOSUN("CHOSUN"),
    YEONHAP("YEONHAP");

    private final String source;
    public String getSource() { return source; }

}
