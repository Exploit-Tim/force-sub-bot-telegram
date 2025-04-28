package org.telegram.forcesub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesub.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    boolean existsByChatIdAndChannelIdAndExpiredAtAfter(String chatId, String channelId, Long expiredAtAfter);

    void deleteByChatIdAndChannelId(String chatId, String channelId);
}
