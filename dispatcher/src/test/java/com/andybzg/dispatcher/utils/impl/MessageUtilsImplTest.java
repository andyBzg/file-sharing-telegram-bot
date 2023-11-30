package com.andybzg.dispatcher.utils.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageUtilsImplTest {

    @Mock
    private Update update;

    @Mock
    private Message message;

    @InjectMocks
    private MessageUtilsImpl messageUtils;

    @Test
    void generateSendMessageWithText_generatesCorrectMessage_success() {
        // given
        String text = "Test message";
        String chatId = "123";
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(123L);

        // when
        SendMessage generatedMessage = messageUtils.generateSendMessageWithText(update, text);

        // then
        assertEquals(chatId, generatedMessage.getChatId());
        assertEquals(text, generatedMessage.getText());
    }
}
