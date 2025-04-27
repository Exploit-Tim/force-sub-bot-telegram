package org.telegram.forcesub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesub.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {


    List<String> findByUuid(String uuid);
}
