package com.andybzg.dispatcher.service.impl;

import com.andybzg.dispatcher.config.RabbitMqConfig;
import com.andybzg.dispatcher.controller.TelegramBot;
import com.andybzg.dispatcher.service.UpdateProducer;
import com.andybzg.dispatcher.utils.MessageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProcessorImplTest {

    @Mock
    private TelegramBot telegramBot;
    @Mock
    private MessageUtils messageUtils;
    @Mock
    private UpdateProducer updateProducer;
    @Mock
    private RabbitMqConfig rabbitMqConfig;

    @InjectMocks
    private UpdateProcessorImpl updateProcessor;

    @Test
    void processUpdate_withUnsupportedMessageType_success() {
        // given
        Update update = new Update();
        Message message = mock(Message.class);
        update.setMessage(message);
        String unsupportedTypeMsg = "Unsupported message type received";

        when(message.hasText()).thenReturn(false);
        when(message.hasDocument()).thenReturn(false);
        when(message.hasPhoto()).thenReturn(false);
        when(messageUtils.generateSendMessageWithText(update, unsupportedTypeMsg)).thenReturn(new SendMessage());

        // when
        updateProcessor.processUpdate(update);

        // then
        verify(messageUtils, times(1)).generateSendMessageWithText(update, unsupportedTypeMsg);
        verify(telegramBot, times(1)).sendAnswerMessage(any(SendMessage.class));
        verifyNoInteractions(rabbitMqConfig, updateProducer);
    }

    @Test
    void processUpdate_withTextMessage_success() {
        // given
        Update update = new Update();
        Message message = mock(Message.class);
        update.setMessage(message);
        String textMessageUpdateQueue = "text_msg_queue";

        when(message.hasText()).thenReturn(true);
        when(rabbitMqConfig.getTextMessageUpdateQueue()).thenReturn(textMessageUpdateQueue);

        // when
        updateProcessor.processUpdate(update);

        // then
        verify(rabbitMqConfig, times(1)).getTextMessageUpdateQueue();
        verify(updateProducer, times(1)).produce(textMessageUpdateQueue, update);
        verifyNoMoreInteractions(rabbitMqConfig, updateProducer);
        verifyNoInteractions(messageUtils, telegramBot);
    }

    @Test
    void processUpdate_withDocMessage_success() {
        // given
        Update update = new Update();
        Message message = mock(Message.class);
        update.setMessage(message);

        when(message.hasText()).thenReturn(false);
        when(message.hasDocument()).thenReturn(true);

        // when
        updateProcessor.processUpdate(update);

        // then
        verify(rabbitMqConfig, times(1)).getDocMessageUpdateQueue();
        verify(updateProducer, times(1)).produce(any(), any());
        verify(messageUtils, times(1)).generateSendMessageWithText(update, "File received! Processing...");
        verify(telegramBot, times(1)).sendAnswerMessage(any());
        verifyNoMoreInteractions(rabbitMqConfig, updateProducer);
    }

    @Test
    void processUpdate_withPhotoMessage_success() {
        // given
        Update update = new Update();
        Message message = mock(Message.class);
        update.setMessage(message);

        when(message.hasText()).thenReturn(false);
        when(message.hasDocument()).thenReturn(false);
        when(message.hasPhoto()).thenReturn(true);

        // when
        updateProcessor.processUpdate(update);

        // then
        verify(rabbitMqConfig, times(1)).getPhotoMessageUpdateQueue();
        verify(updateProducer, times(1)).produce(any(), any());
        verify(messageUtils, times(1)).generateSendMessageWithText(update, "File received! Processing...");
        verify(telegramBot, times(1)).sendAnswerMessage(any());
        verifyNoMoreInteractions(rabbitMqConfig, updateProducer);
    }

    @Test
    void setView_validData_success() {
        // given
        SendMessage sendMessage = new SendMessage();

        // when
        updateProcessor.setView(sendMessage);

        // then
        verify(telegramBot, times(1)).sendAnswerMessage(sendMessage);
    }
}
