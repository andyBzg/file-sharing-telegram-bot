package com.andybzg.service.impl;

import com.andybzg.dao.AppUserDAO;
import com.andybzg.entity.AppUser;
import com.andybzg.utils.CryptoTool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserActivationServiceImplTest {

    @Mock
    private AppUserDAO appUserDAO;

    @Mock
    private CryptoTool cryptoTool;

    @InjectMocks
    private UserActivationServiceImpl userActivationService;

    @Test
    void activation_validData_success() {
        // given
        String cryptoUserId = "encryptedId";
        Long id = 1L;
        AppUser appUser = new AppUser();

        ArgumentCaptor<AppUser> appUserCaptor = ArgumentCaptor.forClass(AppUser.class);

        when(cryptoTool.idOf(cryptoUserId)).thenReturn(id);
        when(appUserDAO.findById(id)).thenReturn(Optional.of(appUser));

        // when
        boolean expected = userActivationService.activation(cryptoUserId);

        // then
        assertTrue(expected);
        verify(cryptoTool, times(1)).idOf(cryptoUserId);
        verify(appUserDAO, times(1)).findById(id);
        verify(appUserDAO, times(1)).save(appUserCaptor.capture());
        assertTrue(appUserCaptor.getValue().isActive());
    }

    @Test
    void activation_userNotFound_success() {
        // given
        String cryptoUserId = "encryptedId";
        Long id = 1L;

        when(cryptoTool.idOf(cryptoUserId)).thenReturn(id);
        when(appUserDAO.findById(id)).thenReturn(Optional.empty());

        // when
        boolean expected = userActivationService.activation(cryptoUserId);

        // then
        assertFalse(expected);
        verify(cryptoTool, times(1)).idOf(cryptoUserId);
        verify(appUserDAO, times(1)).findById(id);
        verifyNoMoreInteractions(appUserDAO);
    }
}
