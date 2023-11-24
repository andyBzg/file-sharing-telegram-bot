package com.andybzg.dispatcher.service.impl;

import com.andybzg.dispatcher.service.UpdateProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnswerConsumerImplTest {

    @Mock
    private UpdateProcessor updateProcessor;

    @InjectMocks
    private AnswerConsumerImpl answerConsumer;

    @Test
    void consume() {
        // given
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Test message");
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        // when
        answerConsumer.consume(sendMessage);

        // then
        verify(updateProcessor, times(1)).setView(sendMessage);
        verify(updateProcessor, times(1)).setView(messageCaptor.capture());

        assertEquals(sendMessage, messageCaptor.getValue());
        assertEquals(sendMessage.getText(), messageCaptor.getValue().getText());
    }
}
