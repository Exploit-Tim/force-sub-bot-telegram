package org.telegram.forcesub.service;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.forcesub.entity.User;
import org.telegram.forcesub.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    @Getter
    private final Set<String> channelIds;

    public UserService(UserRepository userRepository, @Value("${channel.id}") String channelId) {
        this.userRepository = userRepository;
        this.channelIds = new HashSet<>(List.of(channelId.split(",")));  // Mengonversi channelId menjadi Set
    }


    @Transactional
    public void saveUser(String userId, String channelId) {
        try {
            Long expiredAt = Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli();
            User user = User.builder()
                    .chatId(userId)
                    .expiredAt(expiredAt)
                    .channelId(channelId)
                    .build();
            userRepository.save(user);
            log.info("User saved successfully: {}", user.getChatId() + " " + user.getChannelId() + " " + user.getExpiredAt());
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    public void deleteUser(String userId, String channelId) {
        try {
            userRepository.deleteByChatIdAndChannelId(userId, channelId);
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    @Transactional
    public int countAllUser() {
        Set<User> users = new HashSet<>(userRepository.findAll());
        return users.size();
    }

    @Transactional
    public boolean isUserExist(String userId, String channelId) {
        return userRepository.existsByChatIdAndChannelIdAndExpiredAtAfter(userId, channelId, Instant.now().toEpochMilli());
    }

}

