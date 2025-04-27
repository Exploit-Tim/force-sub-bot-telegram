package org.telegram.forcesub.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messageTexts")
@Builder
@Data
public class MessageText {
    private MessageInformation messageInformation;
    private String text;
}
