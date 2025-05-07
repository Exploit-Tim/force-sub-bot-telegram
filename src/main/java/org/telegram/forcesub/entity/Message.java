package org.telegram.forcesub.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;


/**
 * The Class to mapping message and save it into mongoDB database
 * It's only can save Message ID, and Chat ID, not media or text
 * To Save Storage
 *
 */

@Document(collection = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    private String id;
    @Indexed()
    private String uuid;
    private String messageId;
    private String chatId;// Store the Telegram file ID for the media
}