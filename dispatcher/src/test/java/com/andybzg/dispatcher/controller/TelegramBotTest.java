package com.andybzg.dispatcher.controller;

import com.andybzg.dispatcher.config.BotConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramWebhookBot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramBotTest {

    @Mock
    private BotConfig botConfig;

    @InjectMocks
    private TelegramBot telegramBot;

    @Mock
    private TelegramWebhookBot telegramWebhookBot;

    @Test
    void testGetBotUsername() {
        // given
        when(botConfig.getBotName()).thenReturn("TestBot");

        // when
        String username = telegramBot.getBotUsername();

        // then
        assertEquals("TestBot", username);
    }

    @Test
    void testGetBotToken() {
        // given
        when(botConfig.getBotToken()).thenReturn("TestToken");

        // when
        String token = telegramBot.getBotToken();

        // then
        assertEquals("TestToken", token);
    }

    @Test
    void testGetBotUri() {
        // given
        when(botConfig.getBotUri()).thenReturn("https://example.com/bot");

        // when
        String uri = telegramBot.getBotUri();

        // then
        assertEquals("https://example.com/bot", uri);
    }

    @Test
    void testGetBotPath() {
        // when
        String path = telegramBot.getBotPath();

        // then
        assertEquals("/update", path);
    }
}
