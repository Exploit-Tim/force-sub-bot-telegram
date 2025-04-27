package org.telegram.forcesub.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "users")
public class User {

    private String chatId;
    private Long expiredAt;
    private String channelId;
}
