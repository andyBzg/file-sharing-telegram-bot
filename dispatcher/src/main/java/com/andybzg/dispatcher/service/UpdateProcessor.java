package com.andybzg.dispatcher.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProcessor {

    void processUpdate(Update update);

    void setView(SendMessage sendMessage);
}
