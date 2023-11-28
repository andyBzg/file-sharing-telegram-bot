package com.andybzg.controller;

import com.andybzg.entity.AppDocument;
import com.andybzg.entity.AppPhoto;
import com.andybzg.entity.BinaryContent;
import com.andybzg.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    @Test
    void getDoc_validData_success() throws Exception {
        // given
        String documentId = "123";
        BinaryContent mockBinaryContent = mock(BinaryContent.class);
        AppDocument mockDocument = AppDocument.builder()
                .id(1L)
                .telegramFieldId("someId")
                .docName("testDoc")
                .binaryContent(mockBinaryContent)
                .mimeType("application/pdf")
                .fileSize(1024L)
                .build();

        when(fileService.getDocument(documentId)).thenReturn(mockDocument);
        when(mockBinaryContent.getFileAsArrayOfBytes()).thenReturn("Content as array of bytes".getBytes());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();

        // when, then
        mockMvc.perform(get("/file/get-doc")
                        .param("id", documentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(mockDocument.getMimeType())))
                .andExpect(header().string("Content-disposition", "attachment; filename=" + mockDocument.getDocName()))
                .andExpect(content().bytes(mockDocument.getBinaryContent().getFileAsArrayOfBytes()));

        verify(fileService, times(1)).getDocument(documentId);
    }

    @Test
    void getDoc_invalidDocumentId_failure() throws Exception {
        // given
        String invalidDocumentId = "invalidId";
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();

        when(fileService.getDocument(invalidDocumentId)).thenReturn(null);

        // when, then
        mockMvc.perform(get("/file/get-doc")
                        .param("id", invalidDocumentId))
                .andExpect(status().isBadRequest());

        verify(fileService, times(1)).getDocument(invalidDocumentId);
    }

    @Test
    void getPhoto_validData_success() throws Exception {
        // given
        String photoId = "456";
        BinaryContent mockBinaryContent = mock(BinaryContent.class);
        AppPhoto mockPhoto = AppPhoto.builder()
                .id(2L)
                .telegramFieldId("someId")
                .binaryContent(mockBinaryContent)
                .fileSize(1024)
                .build();

        when(fileService.getPhoto(photoId)).thenReturn(mockPhoto);
        when(mockBinaryContent.getFileAsArrayOfBytes()).thenReturn("Content as array of bytes".getBytes());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();

        // when, then
        mockMvc.perform(get("/file/get-photo")
                        .param("id", photoId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().string("Content-disposition", "attachment;"))
                .andExpect(content().bytes(mockPhoto.getBinaryContent().getFileAsArrayOfBytes()));

        verify(fileService, times(1)).getPhoto(photoId);
    }

    @Test
    void getPhoto_invalidPhotoId_failure() throws Exception {
        // Given
        String invalidPhotoId = "invalidId";
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();

        when(fileService.getPhoto(invalidPhotoId)).thenReturn(null);

        // When and Then
        mockMvc.perform(get("/file/get-photo")
                        .param("id", invalidPhotoId))
                .andExpect(status().isBadRequest());

        verify(fileService, times(1)).getPhoto(invalidPhotoId);
    }
}
