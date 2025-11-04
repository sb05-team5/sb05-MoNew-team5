package com.sprint.project.monew.articleBackup.repository;

import com.sprint.project.monew.articleBackup.dto.ArticleBackupDto;
import com.sprint.project.monew.articleBackup.entity.ArticleBackup;
import com.sprint.project.monew.articleView.entity.ArticleView;

import java.util.List;

public interface ArticleBackupQueryRepository {

    List<ArticleBackupDto> searchForRestore(String from, String to);
}
