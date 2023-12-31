package com.andybzg.service.impl;

import com.andybzg.dao.AppUserDAO;
import com.andybzg.entity.AppUser;
import com.andybzg.service.UserActivationService;
import com.andybzg.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    @Override
    public boolean activation(String cryptoUserId) {
        Long userId = cryptoTool.idOf(cryptoUserId);
        Optional<AppUser> optional = appUserDAO.findById(userId);
        if (optional.isPresent()) {
            AppUser appUser = optional.get();
            appUser.setActive(true);
            appUserDAO.save(appUser);
            return true;
        }
        return false;
    }
}
