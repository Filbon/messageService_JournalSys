package com.example.messageservice_journalsys.Controller;

import com.example.messageservice_journalsys.DTO.MessageDTO;
import com.example.messageservice_journalsys.Service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        messageDTO = new MessageDTO();
        messageDTO.setId(1L);
        messageDTO.setSenderId(1L);
        messageDTO.setRecipientId(2L);
        messageDTO.setSenderName("Sender");
        messageDTO.setRecipientName("Recipient");
        messageDTO.setContent("Hello, this is a test message.");
        messageDTO.setSentDate(LocalDateTime.now());
    }

    @Test
    void testSendMessage_success() throws Exception {
        // Mock the service
        Mockito.when(messageService.sendMessage(any(MessageDTO.class))).thenReturn(messageDTO);

        // Perform the POST request
        mockMvc.perform(post("/api/messages/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "senderId": 1,
                                    "recipientId": 2,
                                    "content": "Hello, this is a test message."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.senderId").value(1))
                .andExpect(jsonPath("$.recipientId").value(2))
                .andExpect(jsonPath("$.senderName").value("Sender"))
                .andExpect(jsonPath("$.recipientName").value("Recipient"))
                .andExpect(jsonPath("$.content").value("Hello, this is a test message."));
    }

    @Test
    void testGetConversationWithUser_success() throws Exception {
        // Mock the service
        List<MessageDTO> messages = Arrays.asList(messageDTO);
        Mockito.when(messageService.getMessagesForUser(eq(1L))).thenReturn(messages);

        // Perform the GET request
        mockMvc.perform(get("/api/messages/conversation/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].senderId").value(1))
                .andExpect(jsonPath("$[0].recipientId").value(2))
                .andExpect(jsonPath("$[0].senderName").value("Sender"))
                .andExpect(jsonPath("$[0].recipientName").value("Recipient"))
                .andExpect(jsonPath("$[0].content").value("Hello, this is a test message."));
    }
}
