package com.andybzg.service.impl;

import com.andybzg.service.MainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceImplTest {

    @Mock
    private MainService mainService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ConsumerServiceImpl consumerService;

    @Test
    void consumeTextMessage_ShouldCallProcessTextMessage() {
        // given
        Update update = new Update();

        // when
        consumerService.consumeTextMessage(update);

        // then
        verify(mainService, times(1)).processTextMessage(update);
    }

    @Test
    void consumeDocMessage_ShouldCallProcessDocMessage() {
        // given
        Update update = new Update();

        // when
        consumerService.consumeDocMessage(update);

        // then
        verify(mainService, times(1)).processDocMessage(update);
    }

    @Test
    void consumePhotoMessage_ShouldCallProcessPhotoMessage() {
        // given
        Update update = new Update();

        // when
        consumerService.consumePhotoMessage(update);

        // then
        verify(mainService, times(1)).processPhotoMessage(update);
    }
}
