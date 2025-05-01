package org.telegram.forcesub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesub.entity.Message;

import java.util.List;
import java.util.Optional;


@Repository
public interface MessageRepository extends MongoRepository<Message, String> {


    List<Message> findAllByUuid(String uuid);


    List<Message> findByMessageIdGreaterThanEqual(String messageId);

    Optional<Message> findByUuid(String uuid);
}
