package com.andybzg.dao;

import com.andybzg.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByTelegramUserId(Long telegramUserId);

    Optional<AppUser> findByEmail(String email);
}
