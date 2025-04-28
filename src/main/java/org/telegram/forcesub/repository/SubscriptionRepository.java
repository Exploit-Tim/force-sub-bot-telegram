package org.telegram.forcesub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesub.entity.Subcription;

import java.util.List;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subcription, String> {
    List<Subcription> findByChannelId(String channelId);
}
