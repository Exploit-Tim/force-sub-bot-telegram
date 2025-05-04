package org.telegram.forcesub.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class mapping user
 * Saved data include chat ID, expiredAt, and Channel ID they Joined
 * This will reduce call to Telegram API Each bot sending request
 * param @expiredAt added to check certains time, use case in Service Layer
 */

@Data
@Builder
@Document(collection = "users")
public class User {

    private String chatId;
    private Long expiredAt;
    private String channelId;
}
