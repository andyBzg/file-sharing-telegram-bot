package com.andybzg.service;

import com.andybzg.entity.AppDocument;
import com.andybzg.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {

    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramMessage);

}
