package com.andybzg.service;

import com.andybzg.entity.AppDocument;
import com.andybzg.entity.AppPhoto;

public interface FileService {

    AppDocument getDocument(String id);

    AppPhoto getPhoto(String id);
}
