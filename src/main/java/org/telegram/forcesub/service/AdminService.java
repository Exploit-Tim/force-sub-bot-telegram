package org.telegram.forcesub.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.forcesub.entity.Admin;
import org.telegram.forcesub.entity.Roles;
import org.telegram.forcesub.repository.AdminsRepository;

import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    private final List<String> adminList;

    private final String ownerUserId;

    private final AdminsRepository adminsRepository;

    public AdminService(AdminsRepository adminsRepository,
                        @Value("${admin.id}") String adminIds,
                        @Value("${owner.userid}") String ownerUserId) {
        this.adminsRepository = adminsRepository;
        this.adminList = List.of(adminIds.trim().split(","));
        this.ownerUserId = ownerUserId;
    }
    public void saveAdmin(String chatId) {
        Admin admin = Admin.builder()
                .chatId(chatId)
                .role(Roles.ADMIN)
                .build();
        adminsRepository.save(admin);
    }

    public Admin getAdmin(String chatId) {
        return adminsRepository.findByChatId(chatId);
    }

    public boolean isAdminExist(String chatId) {
        return !adminsRepository.existsByChatId(chatId);
    }

    public void deleteAdmin(String chatId) {
        adminsRepository.deleteByChatId(chatId);
    }
    public Set<String> countAllAdmin() {
        List<Admin> all = adminsRepository.findAll();
        return Set.copyOf(all.stream().map(Admin::getChatId).toList());
    }

    @PostConstruct
    public void setUpAdmin() {
        adminsRepository.deleteAll();

        adminList.stream()
                .filter(this::isAdminExist)
                .map(this::buildAdmin)
                .forEach(adminsRepository::save);

        adminsRepository.save(Admin.builder()
                .role(Roles.OWNER)
                .chatId(ownerUserId)
                .build());
    }

    private Admin buildAdmin(String chatId) {
        return Admin.builder()
                .chatId(chatId)
                .role(Roles.ADMIN)
                .build();
    }

}
