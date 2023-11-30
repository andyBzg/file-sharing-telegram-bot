package com.andybzg.service.impl;

import com.andybzg.dao.AppUserDAO;
import com.andybzg.entity.AppUser;
import com.andybzg.enums.UserState;
import com.andybzg.utils.CryptoTool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock
    private AppUserDAO appUserDAO;

    @Mock
    private CryptoTool cryptoTool;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    @Test
    void registerUser_registeredUser_success() {
        // given
        String expected = messageSource.getMessage("activation.already.registered", null, LocaleContextHolder.getLocale());
        AppUser appUser = AppUser.builder()
                .isActive(true)
                .email("example@mail.com")
                .build();

        // when
        String actual = appUserService.registerUser(appUser);

        // then
        assertEquals(expected, actual);
        verifyNoInteractions(appUserDAO);
    }

    @Test
    void registerUser_unregisteredUser_success() {
        // given
        String expected = messageSource.getMessage("registration.wait.email", null, LocaleContextHolder.getLocale());
        UserState expectedState = UserState.WAIT_FOR_EMAIL_STATE;
        AppUser appUser = AppUser.builder()
                .isActive(false)
                .email(null)
                .build();
        ArgumentCaptor<AppUser> appUserCaptor = ArgumentCaptor.forClass(AppUser.class);

        // when
        String actual = appUserService.registerUser(appUser);

        // then
        assertEquals(expected, actual);
        verify(appUserDAO, times(1)).save(appUserCaptor.capture());
        UserState actualState = appUserCaptor.getValue().getState();
        assertEquals(expectedState, actualState);
    }

    @Test
    void registerUser_userWaitingForEmailVerification_success() {
        // given
        String expected = messageSource.getMessage("activation.email.sent", null, LocaleContextHolder.getLocale());
        AppUser appUser = AppUser.builder()
                .isActive(false)
                .email("example@mail.com")
                .build();

        // when
        String actual = appUserService.registerUser(appUser);

        // then
        assertEquals(expected, actual);
        verifyNoInteractions(appUserDAO);
    }

    @Test
    void setEmail_invalidEmail_success() {
        // given
        String expected = messageSource.getMessage("email.invalid", null, LocaleContextHolder.getLocale());
        String email = "invalid_email";
        AppUser appUser = new AppUser();

        // when
        String actual = appUserService.setEmail(appUser, email);

        // then
        assertEquals(expected, actual);
        verifyNoInteractions(appUserDAO, cryptoTool);
    }

    @Test
    void setEmail_emailAlreadyUsed_success() {
        // given
        String expected = messageSource.getMessage("email.used", null, LocaleContextHolder.getLocale());
        String email = "example@mail.com";
        AppUser appUser = new AppUser();

        when(appUserDAO.findByEmail(email)).thenReturn(Optional.of(appUser));

        // when
        String actual = appUserService.setEmail(appUser, email);

        // then
        assertEquals(expected, actual);
        verify(appUserDAO, times(1)).findByEmail(email);
        verifyNoMoreInteractions(appUserDAO);
        verifyNoInteractions(cryptoTool);
    }
}
