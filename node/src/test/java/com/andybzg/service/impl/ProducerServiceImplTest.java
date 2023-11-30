package com.andybzg.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProducerServiceImplTest {

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ProducerServiceImpl producerService;

    @Test
    void produceAnswer() {
        // given
        SendMessage sendMessage = new SendMessage();

        // when
        producerService.produceAnswer(sendMessage);

        // then
        verify(rabbitTemplate).convertAndSend(answerMessageQueue, sendMessage);
    }
}