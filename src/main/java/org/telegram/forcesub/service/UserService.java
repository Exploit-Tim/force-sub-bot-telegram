package org.telegram.forcesub.service;

import lombok.Getter;
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

    private final UserRepository userRepository;
    @Getter
    private final Set<String> channelIds;

    public UserService(UserRepository userRepository, @Value("${channel.id}") String channelId) {
        this.userRepository = userRepository;
        this.channelIds = new HashSet<>(List.of(channelId.split(",")));  // Mengonversi channelId menjadi Set
    }


    @Transactional
    public String saveUser(String userId, String channelId) {
        try {
            Long expiredAt = Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli();
            User user = User.builder()
                    .chatId(userId)
                    .expiredAt(expiredAt)
                    .channelId(channelId)
                    .build();
            userRepository.save(user);
            return "User saved successfully";
        } catch (Exception e) {
            return "Error saving user";
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

    public boolean[] isExistAndExpired(String userId) {
        boolean[] result = new boolean[channelIds.size()];
        int i = 0;
        for (String channel : channelIds) {
            boolean expired = userRepository.existsByChatIdAndChannelIdAndExpiredAtBefore(userId, channel, Instant.now().toEpochMilli());
            if (expired) {
                deleteUser(userId, channel);
            }
            result[i++] = expired;
        }

        return result;
    }

}

