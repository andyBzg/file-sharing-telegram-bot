package com.andybzg.dispatcher.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProducerImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private UpdateProducerImpl updateProducer;

    @Test
    void produce_shouldSendMessageToRabbitQueue_success() {
        // given
        String rabbitQueue = "testQueue";
        Update update = mock(Update.class);
        Message message = new Message();
        when(update.getMessage()).thenReturn(message);
        ArgumentCaptor<String> queueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);

        // when
        updateProducer.produce(rabbitQueue, update);

        // then
        verify(rabbitTemplate, times(1)).convertAndSend(queueCaptor.capture(), updateCaptor.capture());

        assertEquals(rabbitQueue, queueCaptor.getValue());
        assertEquals(update, updateCaptor.getValue());
    }
}
