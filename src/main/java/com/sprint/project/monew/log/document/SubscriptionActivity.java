package com.sprint.project.monew.log.document;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "subscriptions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class SubscriptionActivity {

    @Id
    private String id = UUID.randomUUID().toString();

    @Field("created_at")
    private Instant createdAt;
    @Field("interest_id")
    private String interestId;

    private String interestName;

    private List<String> interestKeywords;

    private Integer interestSubscripberCount;

    @Field("user_id")
    private String userId; //보고 보류

}
