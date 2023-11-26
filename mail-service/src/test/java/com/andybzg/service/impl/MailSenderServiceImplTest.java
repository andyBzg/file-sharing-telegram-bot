package com.andybzg.service.impl;

import com.andybzg.dto.MailParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${server.activation.uri}")
    private String activationServiceUri;

    @InjectMocks
    private MailSenderServiceImpl mailSenderService;

    @Test
    void send_success() {
        // given
        String userId = "123";
        String emailTo = "example@mail.com";
        String subject = "Account activation";
        String expectedBody = String.format("Please complete your registration:%n%s", activationServiceUri);

        MailParams mailParams = new MailParams(userId, emailTo);

        ArgumentCaptor<SimpleMailMessage> argument = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // when
        mailSenderService.send(mailParams);

        // then
        verify(javaMailSender, times(1)).send(argument.capture());
        SimpleMailMessage capturedMessage = argument.getValue();
        assertEquals(emailFrom, capturedMessage.getFrom());
        assertEquals(mailParams.emailTo(), Objects.requireNonNull(capturedMessage.getTo())[0]);
        assertEquals(subject, capturedMessage.getSubject());
        assertTrue(Objects.requireNonNull(capturedMessage.getText()).contains(expectedBody));
        assertEquals(expectedBody.replace("{id}", userId), capturedMessage.getText());
    }
}
