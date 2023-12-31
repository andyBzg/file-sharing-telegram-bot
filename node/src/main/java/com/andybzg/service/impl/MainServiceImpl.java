package com.andybzg.service.impl;

import com.andybzg.dao.AppUserDAO;
import com.andybzg.dao.RawDataDAO;
import com.andybzg.entity.AppDocument;
import com.andybzg.entity.AppPhoto;
import com.andybzg.entity.AppUser;
import com.andybzg.entity.RawData;
import com.andybzg.enums.UserState;
import com.andybzg.exceptions.UploadFileException;
import com.andybzg.service.AppUserService;
import com.andybzg.service.FileService;
import com.andybzg.service.MainService;
import com.andybzg.service.ProducerService;
import com.andybzg.service.enums.LinkType;
import com.andybzg.service.enums.ServiceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;
    private final MessageSource messageSource;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        UserState userState = appUser.getState();
        String text = update.getMessage().getText();
        String output;

        ServiceCommand command = ServiceCommand.fromValue(text);
        if (ServiceCommand.CANCEL.equals(command)) {
            output = cancelProcess(appUser);
        } else if (UserState.BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (UserState.WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
        } else {
            log.error(messageSource.getMessage(
                    "log.unknown.state", new Object[]{userState}, LocaleContextHolder.getLocale()));
            output = messageSource.getMessage("error.msg.unknown", null, LocaleContextHolder.getLocale());
        }

        Long chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument document = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(document.getId(), LinkType.GET_DOC);
            String answer = messageSource.getMessage(
                    "doc.upload.success", new Object[]{link}, LocaleContextHolder.getLocale());
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex.getMessage());
            String error = messageSource.getMessage("file.upload.failure", null, LocaleContextHolder.getLocale());
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();
        if (isNotAllowedToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            String answer = messageSource.getMessage(
                    "photo.upload.success", new Object[]{link}, LocaleContextHolder.getLocale());
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex.getMessage());
            String error = messageSource.getMessage("file.upload.failure", null, LocaleContextHolder.getLocale());
            sendAnswer(error, chatId);
        }

    }

    private boolean isNotAllowedToSendContent(Long chatId, AppUser appUser) {
        UserState userState = appUser.getState();
        if (!appUser.isActive()) {
            String error = messageSource.getMessage(
                    "error.msg.not-registered", null, LocaleContextHolder.getLocale());
            sendAnswer(error, chatId);
            return true;
        } else if (!UserState.BASIC_STATE.equals(userState)) {
            String error = messageSource.getMessage(
                    "error.msg.not-allowed-to-upload", null, LocaleContextHolder.getLocale());
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if (ServiceCommand.REGISTRATION.equals(cmd)) {
            return appUserService.registerUser(appUser);
        } else if (ServiceCommand.HELP.equals(cmd)) {
            return help();
        } else if (ServiceCommand.START.equals(cmd)) {
            return messageSource.getMessage("command.response.start", null, LocaleContextHolder.getLocale());
        } else {
            return messageSource.getMessage("command.response.unknown", null, LocaleContextHolder.getLocale());
        }
    }

    private String help() {
        return messageSource.getMessage("command.response.help", null, LocaleContextHolder.getLocale());
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(UserState.BASIC_STATE);
        appUserDAO.save(appUser);
        return messageSource.getMessage("command.response.cancel", null, LocaleContextHolder.getLocale());
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        Optional<AppUser> persistentAppUser = appUserDAO.findByTelegramUserId(telegramUser.getId());

        if (persistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastname(telegramUser.getLastName())
                    .isActive(false)
                    .state(UserState.BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persistentAppUser.get();
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
