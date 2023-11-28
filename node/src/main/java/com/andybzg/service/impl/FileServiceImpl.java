package com.andybzg.service.impl;

import com.andybzg.dao.AppDocumentDAO;
import com.andybzg.dao.AppPhotoDAO;
import com.andybzg.dao.BinaryContentDAO;
import com.andybzg.entity.AppDocument;
import com.andybzg.entity.AppPhoto;
import com.andybzg.entity.BinaryContent;
import com.andybzg.utils.CryptoTool;
import com.andybzg.exceptions.UploadFileException;
import com.andybzg.service.FileService;
import com.andybzg.service.enums.LinkType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@PropertySource(value = "classpath:telegram-api.properties")
@Service
public class FileServiceImpl implements FileService {

    @Value("${bot.token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAddress;

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final ObjectMapper objectMapper;
    private final CryptoTool cryptoTool;
    private final MessageSource messageSource;

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDoc = telegramMessage.getDocument();
        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);

            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        } else {
            String msg = messageSource.getMessage(
                    "exception.upload.bad-response", new Object[]{response}, LocaleContextHolder.getLocale());
            throw new UploadFileException(msg);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        //TODO implement multiple photo processing
        int photoSizeCount = telegramMessage.getPhoto().size();
        int photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);

        if (HttpStatus.OK.equals(response.getStatusCode())) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);

            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        } else {
            String msg = messageSource.getMessage(
                    "exception.upload.bad-response", new Object[]{response}, LocaleContextHolder.getLocale());
            throw new UploadFileException(msg);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        String hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private String getFilePath(ResponseEntity<String> response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode
                    .path("result")
                    .path("file_path")
                    .asText();
        } catch (JsonProcessingException e) {
            String msg = messageSource.getMessage(
                    "exception.upload.failed-to-process", null, LocaleContextHolder.getLocale());
            throw new UploadFileException(msg, e);
        }
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFieldId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFieldId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri
                .replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        //TODO optimization needed
        try (InputStream inputStream = urlObj.openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,
                fileId
        );
    }
}
