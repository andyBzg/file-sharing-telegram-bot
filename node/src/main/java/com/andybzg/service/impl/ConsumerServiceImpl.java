package com.andybzg.service.impl;

import com.andybzg.service.ConsumerService;
import com.andybzg.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;
    private final MessageSource messageSource;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.text-message-update}")
    public void consumeTextMessage(Update update) {
        log.info(messageSource.getMessage("log.node.msg.text", null, LocaleContextHolder.getLocale()));
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.doc-message-update}")
    public void consumeDocMessage(Update update) {
        log.info(messageSource.getMessage("log.node.msg.doc", null, LocaleContextHolder.getLocale()));
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.photo-message-update}")
    public void consumePhotoMessage(Update update) {
        log.info(messageSource.getMessage("log.node.msg.photo", null, LocaleContextHolder.getLocale()));
        mainService.processPhotoMessage(update);
    }
}
