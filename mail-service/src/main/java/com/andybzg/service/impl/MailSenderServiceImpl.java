package com.andybzg.service.impl;

import com.andybzg.dto.MailParams;
import com.andybzg.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@PropertySource("classpath:email.properties")
@Service
public class MailSenderServiceImpl implements MailSenderService {

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${server.activation.uri}")
    private String activationServiceUri;

    private final JavaMailSender javaMailSender;

    @Override
    public void send(MailParams mailParams) {
        String subject = "Account activation";
        String messageBody = getActivationMailBody(mailParams.id());
        String emailTo = mailParams.emailTo();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(messageBody);

        javaMailSender.send(mailMessage);
    }

    private String getActivationMailBody(String id) {
        String message = String.format("Please complete your registration:%n%s", activationServiceUri);
        return message.replace("{id}", id);
    }
}
