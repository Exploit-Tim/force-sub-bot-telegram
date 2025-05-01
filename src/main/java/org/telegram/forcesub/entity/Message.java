package org.telegram.forcesub.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;


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
    private String chatId;
}