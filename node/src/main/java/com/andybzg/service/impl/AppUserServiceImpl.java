package com.andybzg.service.impl;

import com.andybzg.dao.AppUserDAO;
import com.andybzg.dto.MailParams;
import com.andybzg.entity.AppUser;
import com.andybzg.enums.UserState;
import com.andybzg.exceptions.InvalidEmailException;
import com.andybzg.service.AppUserService;
import com.andybzg.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {

    @Value("${service.mail.uri}")
    private String mailServiceUri;

    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;
    private final MessageSource messageSource;

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.isActive()) {
            return messageSource.getMessage("activation.already.registered", null, LocaleContextHolder.getLocale());
        } else if (appUser.getEmail() != null) {
            return messageSource.getMessage("activation.email.sent", null, LocaleContextHolder.getLocale());
        }
        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return messageSource.getMessage("registration.wait.email", null, LocaleContextHolder.getLocale());
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            validateEmail(email);

            Optional<AppUser> optional = appUserDAO.findByEmail(email);
            if (optional.isEmpty()) {
                return processNewEmail(appUser, email);
            } else {
                return messageSource.getMessage("email.used", null, LocaleContextHolder.getLocale());
            }
        } catch (InvalidEmailException ex) {
            return messageSource.getMessage("email.invalid", null, LocaleContextHolder.getLocale());
        }
    }

    private void validateEmail(String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            String errorMsg = messageSource.getMessage(
                    "exception.invalid.email", null, LocaleContextHolder.getLocale());
            throw new InvalidEmailException(errorMsg, ex);
        }
    }

    private String processNewEmail(AppUser appUser, String email) {
        appUser.setEmail(email);
        appUser.setState(UserState.BASIC_STATE);
        appUser = appUserDAO.save(appUser);

        String cryptoUserId = cryptoTool.hashOf(appUser.getId());
        MailParams mailParams = new MailParams(cryptoUserId, email);

        ResponseEntity<String> response = sendRequestToMailService(mailParams);

        if (response.getStatusCode() == HttpStatus.OK) {
            return messageSource.getMessage("email.activation.sent", null, LocaleContextHolder.getLocale());
        } else {
            handleFailedEmailSending(appUser, email);
            return messageSource.getMessage("email.send.error", null, LocaleContextHolder.getLocale());
        }
    }

    private ResponseEntity<String> sendRequestToMailService(MailParams mailParams) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MailParams> request = new HttpEntity<>(mailParams, headers);

        return restTemplate.exchange(
                mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);
    }

    private void handleFailedEmailSending(AppUser appUser, String email) {
        log.error(messageSource.getMessage(
                "log.unable-to-send", new Object[]{email}, LocaleContextHolder.getLocale()));
        appUser.setEmail(null);
        appUserDAO.save(appUser);
    }
}
