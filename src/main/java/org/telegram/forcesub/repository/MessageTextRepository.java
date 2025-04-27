package org.telegram.forcesub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesub.entity.MessageInformation;
import org.telegram.forcesub.entity.MessageText;

@Repository
public interface MessageTextRepository extends MongoRepository<MessageText, String> {

    MessageText getMessageTextsByMessageInformation(MessageInformation messageInformation);

}
