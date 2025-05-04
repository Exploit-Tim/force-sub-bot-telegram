package org.telegram.forcesub.entity;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * The Class To Save admin chat Id,
 * They can control the bot, generate message Id, and can create broadcast message
 */
@Data
@Builder
@Document(collection = "admins")
public class Admin {

    private ObjectId id;

    private String chatId;

    private Roles role;
}
