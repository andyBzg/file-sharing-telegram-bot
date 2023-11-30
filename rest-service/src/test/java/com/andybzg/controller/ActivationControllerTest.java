package com.andybzg.controller;

import com.andybzg.service.UserActivationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivationControllerTest {

    @Mock
    private UserActivationService userActivationService;

    @InjectMocks
    private ActivationController activationController;

    @Test
    void activation_validDate_success() {
        // given
        String userId = "123";
        when(userActivationService.activation(userId)).thenReturn(true);

        // when
        ResponseEntity<?> responseEntity = activationController.activation(userId);

        // then
        verify(userActivationService, times(1)).activation(userId);
        assertAll(
                () -> assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> assertEquals("Successfully registered", responseEntity.getBody())
        );
    }

    @Test
    void activation_invalidUserId_failure() {
        // given
        String userId = "InvalidID";
        when(userActivationService.activation(userId)).thenReturn(false);

        // when
        ResponseEntity<?> responseEntity = activationController.activation(userId);

        // then
        verify(userActivationService, times(1)).activation(userId);
        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode()),
                () -> assertNull(responseEntity.getBody())
        );
    }
}
