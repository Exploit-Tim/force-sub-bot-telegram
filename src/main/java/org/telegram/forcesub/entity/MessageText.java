package org.telegram.forcesub.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * This class retrieve all message in Database
 * Based On Given Message Information
 */

@Document(collection = "messageTexts")
@Builder
@Data
public class MessageText {
    private MessageInformation messageInformation;
    private String text;
}
