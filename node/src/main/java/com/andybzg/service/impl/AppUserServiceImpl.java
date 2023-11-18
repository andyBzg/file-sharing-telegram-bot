package com.andybzg.service.impl;

import com.andybzg.dao.AppUserDAO;
import com.andybzg.dto.MailParams;
import com.andybzg.entity.AppUser;
import com.andybzg.enums.UserState;
import com.andybzg.service.AppUserService;
import com.andybzg.utils.CryptoTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Service
@Slf4j
public class AppUserServiceImpl implements AppUserService {

    @Value("${service.mail.uri}")
    private String mailServiceUri;

    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.isActive()) {
            return "You are already registered!";
        } else if (appUser.getEmail() != null) {
            return "Activation email has been already sent. Please check your inbox";
        }
        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Please enter email";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            return "Please enter correct email. Type /cancel to exit";
        }

        Optional<AppUser> optional = appUserDAO.findByEmail(email);
        if (optional.isEmpty()) {
            appUser.setEmail(email);
            appUser.setState(UserState.BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            String cryptoUserId = cryptoTool.hashOf(appUser.getId());
            ResponseEntity<String> response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                String message = String.format("Unable to send email to %s,", email);
                log.error(message);
                appUser.setEmail(null);
                appUserDAO.save(appUser);
                return message;
            }
            return "Activation link has been sent to your email. Please check your inbox";
        } else {
            return "This email is already used. Please enter correct email. Type /cancel to exit";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        MailParams mailParams = new MailParams(
                cryptoUserId,
                email
        );
        HttpEntity<MailParams> request = new HttpEntity<>(mailParams, headers);
        return restTemplate.exchange(
                mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);
    }
}
