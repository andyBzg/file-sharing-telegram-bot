package com.andybzg.dispatcher.config;

import com.andybzg.dispatcher.controller.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor
@Component
public class BotInitializer {

    private final TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        try {
            SetWebhook setWebhook = SetWebhook.builder()
                    .url(telegramBot.getBotUri())
                    .build();
            telegramBot.setWebhook(setWebhook);
        } catch (
                TelegramApiException ex) {
            log.error(ex.getMessage());
        }
    }
}
