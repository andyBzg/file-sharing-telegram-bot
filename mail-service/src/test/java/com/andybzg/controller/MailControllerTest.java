package com.andybzg.controller;

import com.andybzg.dto.MailParams;
import com.andybzg.service.MailSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MailControllerTest {

    @Mock
    private MailSenderService mailSenderService;

    @InjectMocks
    private MailController mailController;

    @Test
    void sendActivationMail_validData_success() {
        // given
        MailParams mailParams = new MailParams("1", "example@mail.com");

        // when
        ResponseEntity<?> responseEntity = mailController.sendActivationMail(mailParams);

        // then
        verify(mailSenderService, times(1)).send(mailParams);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testSendEndpoint_MappingAndMethod() throws Exception {
        // given
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(mailController).build();

        // when, then
        mockMvc.perform(post("/mail/send")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
