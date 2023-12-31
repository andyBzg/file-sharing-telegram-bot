package com.andybzg.controller;

import com.andybzg.entity.AppDocument;
import com.andybzg.entity.AppPhoto;
import com.andybzg.entity.BinaryContent;
import com.andybzg.service.FileService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/file")
@RestController
public class FileController {

    private final FileService fileService;

    @GetMapping("/get-doc")
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response) {
        AppDocument document = fileService.getDocument(id);

        if (document == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType(String.valueOf(MediaType.parseMediaType(document.getMimeType())));
        response.setHeader("Content-disposition", "attachment; filename=" + document.getDocName());

        BinaryContent binaryContent = document.getBinaryContent();

        createHttpServletResponse(response, binaryContent);
    }

    @GetMapping("/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response) {
        AppPhoto photo = fileService.getPhoto(id);

        if (photo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-disposition", "attachment;");

        BinaryContent binaryContent = photo.getBinaryContent();

        createHttpServletResponse(response, binaryContent);
    }

    private void createHttpServletResponse(HttpServletResponse response, BinaryContent binaryContent) {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(binaryContent.getFileAsArrayOfBytes());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
