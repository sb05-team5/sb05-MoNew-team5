package com.sprint.project.monew.articleBackup.backupBatch.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class InterestKeywordDto {
    private UUID interestId;
    private String keyword;
}
