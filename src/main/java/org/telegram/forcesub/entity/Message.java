package org.telegram.forcesub.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "messages")
public class Message {

    @Id
    private String uuid;
    private String messageId;
    private String chatId;



}
