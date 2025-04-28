package org.telegram.forcesub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesub.entity.Message;


@Repository
public interface MessageRepository extends MongoRepository<Message, String> {


    Message findByUuid(String uuid);
}
