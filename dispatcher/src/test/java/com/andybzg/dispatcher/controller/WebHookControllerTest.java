package com.andybzg.dispatcher.controller;

import com.andybzg.dispatcher.service.UpdateProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebHookController.class)
class WebHookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UpdateProcessor updateProcessor;

    @Test
    void onUpdateReceived_validData_success() throws Exception {
        // given
        Update update = new Update();
        update.setMessage(new Message());

        ObjectMapper objectMapper = new ObjectMapper();
        String updateJson = objectMapper.writeValueAsString(update);

        // when
        mockMvc.perform(post("/callback/update")
                        .content(updateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        verify(updateProcessor, times(1)).processUpdate(update);
    }

    @Test
    void onUpdateReceived_returnsStatusOk_success() throws Exception {
        mockMvc.perform(post("/callback/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void onUpdateReceived_invalidJsonContent_returnsStatus400() throws Exception {
        String invalidJsonContent = "";

        mockMvc.perform(post("/callback/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonContent))
                .andExpect(status().isBadRequest());
    }
}
