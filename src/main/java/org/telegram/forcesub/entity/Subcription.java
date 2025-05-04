package org.telegram.forcesub.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * In The first Application Running, save initial data
 * In this case, save channel Links in database,
 * Not Get Channel Link every need
 * Reduce Call to Telegram API, and reduce limit
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "subcription")
public class Subcription {

    private String channelId;
    private String channelLink;
}
