package com.andybzg.service.impl;

import com.andybzg.dao.AppDocumentDAO;
import com.andybzg.dao.AppPhotoDAO;
import com.andybzg.entity.AppDocument;
import com.andybzg.entity.AppPhoto;
import com.andybzg.utils.CryptoTool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private AppDocumentDAO appDocumentDAO;

    @Mock
    private AppPhotoDAO appPhotoDAO;

    @Mock
    private CryptoTool cryptoTool;

    @InjectMocks
    private FileServiceImpl fileService;

    @Test
    void getDocument_validData_success() {
        // given
        String hash = "SomeHash";
        Long id = 1L;
        AppDocument appDocument = AppDocument.builder()
                .id(id)
                .build();
        when(cryptoTool.idOf(hash)).thenReturn(id);
        when(appDocumentDAO.findById(id)).thenReturn(Optional.of(appDocument));

        // when
        AppDocument expected = fileService.getDocument(hash);

        // then
        verify(cryptoTool, times(1)).idOf(hash);
        verify(appDocumentDAO, times(1)).findById(id);
        assertNotNull(expected);
        assertEquals(expected, appDocument);
        assertEquals(expected.getId(), id);
    }

    @Test
    void getDocument_nullId_returnsNull() {
        // given
        String hash = "someHash";
        when(cryptoTool.idOf(hash)).thenReturn(null);

        // when
        AppDocument result = fileService.getDocument(hash);

        // then
        assertNull(result);
        verify(cryptoTool, times(1)).idOf(hash);
        verifyNoInteractions(appDocumentDAO);
    }

    @Test
    void getPhoto_validData_success() {
        // given
        String hash = "SomeHash";
        Long id = 1L;
        AppPhoto appPhoto = AppPhoto.builder()
                .id(id)
                .build();
        when(cryptoTool.idOf(hash)).thenReturn(id);
        when(appPhotoDAO.findById(id)).thenReturn(Optional.of(appPhoto));

        // when
        AppPhoto expected = fileService.getPhoto(hash);

        // then
        verify(cryptoTool, times(1)).idOf(hash);
        verify(appPhotoDAO, times(1)).findById(id);
        assertNotNull(expected);
        assertEquals(expected.getId(), id);
        assertEquals(expected, appPhoto);
    }

    @Test
    void getPhoto_nullId_returnsNull() {
        // given
        String hash = "someHash";
        when(cryptoTool.idOf(hash)).thenReturn(null);

        // when
        AppPhoto result = fileService.getPhoto(hash);

        // then
        assertNull(result);
        verify(cryptoTool, times(1)).idOf(hash);
        verifyNoInteractions(appDocumentDAO);
    }
}
