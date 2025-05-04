package org.telegram.forcesub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesub.entity.Admin;

@Repository
public interface AdminsRepository extends MongoRepository<Admin, String> {

    boolean existsByChatId(String chatId);

    void deleteByChatId(String chatId);

    Admin findByChatId(String chatId);
}
