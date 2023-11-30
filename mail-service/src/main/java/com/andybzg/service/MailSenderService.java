package com.andybzg.service;

import com.andybzg.dto.MailParams;

public interface MailSenderService {

    void send(MailParams mailParams);

}
