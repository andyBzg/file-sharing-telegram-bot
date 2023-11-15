package com.andybzg.dao;

import com.andybzg.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {

    AppUser findAppUserByTelegramUserId(Long telegramUserId);
}
