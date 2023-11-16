package com.andybzg.service;

import com.andybzg.entity.AppDocument;
import com.andybzg.entity.AppPhoto;
import com.andybzg.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {

    AppDocument getDocument(String id);

    AppPhoto getPhoto(String id);

    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
