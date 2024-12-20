package com.example.messageservice_journalsys.Service;

import com.example.messageservice_journalsys.DTO.MessageDTO;
import com.example.messageservice_journalsys.DTO.UserDTO;
import com.example.messageservice_journalsys.Model.Message;
import com.example.messageservice_journalsys.Repository.MessageRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MessageService messageService;

    @Test
    void testSendMessage_success() {
        // Arrange
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setSenderId(1L);
        messageDTO.setRecipientId(2L);
        messageDTO.setContent("Hello!");

        UserDTO sender = new UserDTO();
        sender.setId(1L);
        sender.setUserName("SenderUser");

        UserDTO recipient = new UserDTO();
        recipient.setId(2L);
        recipient.setUserName("RecipientUser");

        Message message = new Message();
        message.setId(1L);
        message.setContent("Hello!");
        message.setSenderId(1L);
        message.setRecipientId(2L);
        message.setTimestamp(LocalDateTime.now());

        when(restTemplate.exchange(
                eq("https://userservice.app.cloud.cbh.kth.se/api/user/users/{id}"),
                eq(HttpMethod.GET),
                isNull(),
                eq(UserDTO.class),
                eq(1L)
        )).thenReturn(new ResponseEntity<>(sender, HttpStatus.OK));

        when(restTemplate.exchange(
                eq("https://userservice.app.cloud.cbh.kth.se/api/user/users/{id}"),
                eq(HttpMethod.GET),
                isNull(),
                eq(UserDTO.class),
                eq(2L)
        )).thenReturn(new ResponseEntity<>(recipient, HttpStatus.OK));

        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(modelMapper.map(any(Message.class), eq(MessageDTO.class))).thenReturn(messageDTO);

        // Act
        MessageDTO result = messageService.sendMessage(messageDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Hello!", result.getContent());
        assertEquals("SenderUser", result.getSenderName());
        assertEquals("RecipientUser", result.getRecipientName());

        verify(messageRepository, times(1)).save(any(Message.class));
        verify(restTemplate, times(2)).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(UserDTO.class), anyLong());
    }

    @Test
    void testSendMessage_senderNotFound() {
        // Arrange
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setSenderId(1L);
        messageDTO.setRecipientId(2L);

        when(restTemplate.exchange(
                eq("https://userservice.app.cloud.cbh.kth.se/api/user/users/{id}"),
                eq(HttpMethod.GET),
                isNull(),
                eq(UserDTO.class),
                eq(1L)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            messageService.sendMessage(messageDTO);
        });

        assertEquals("Sender not found with ID: 1", exception.getMessage());
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void testGetMessagesForUser_success() {
        // Arrange
        Long userId = 1L;

        Message message1 = new Message();
        message1.setId(1L);
        message1.setSenderId(1L);
        message1.setRecipientId(2L);
        message1.setContent("Hello!");

        Message message2 = new Message();
        message2.setId(2L);
        message2.setSenderId(2L);
        message2.setRecipientId(1L);
        message2.setContent("Hi!");

        List<Message> messages = Arrays.asList(message1, message2);

        UserDTO sender1 = new UserDTO();
        sender1.setId(1L);
        sender1.setUserName("SenderUser");

        UserDTO recipient1 = new UserDTO();
        recipient1.setId(2L);
        recipient1.setUserName("RecipientUser");

        UserDTO sender2 = new UserDTO();
        sender2.setId(2L);
        sender2.setUserName("RecipientUser");

        UserDTO recipient2 = new UserDTO();
        recipient2.setId(1L);
        recipient2.setUserName("SenderUser");

        when(messageRepository.findMessagesByUserId(userId)).thenReturn(messages);

        // For userId 1
        when(restTemplate.exchange(
                eq("https://userservice.app.cloud.cbh.kth.se/api/user/users/{id}"),
                eq(HttpMethod.GET),
                isNull(),
                eq(UserDTO.class),
                eq(1L)
        ))
                .thenReturn(new ResponseEntity<>(sender1, HttpStatus.OK)) // First call
                .thenReturn(new ResponseEntity<>(recipient2, HttpStatus.OK)); // Second call

// For userId 2
        when(restTemplate.exchange(
                eq("https://userservice.app.cloud.cbh.kth.se/api/user/users/{id}"),
                eq(HttpMethod.GET),
                isNull(),
                eq(UserDTO.class),
                eq(2L)
        ))
                .thenReturn(new ResponseEntity<>(recipient1, HttpStatus.OK)) // First call
                .thenReturn(new ResponseEntity<>(sender2, HttpStatus.OK)); // Second call


        // Act
        List<MessageDTO> result = messageService.getMessagesForUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Hello!", result.get(0).getContent());
        assertEquals("SenderUser", result.get(0).getSenderName());
        assertEquals("RecipientUser", result.get(0).getRecipientName());

        verify(messageRepository, times(1)).findMessagesByUserId(userId);
        verify(restTemplate, times(4)).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(UserDTO.class), anyLong());
    }


    @Test
    void testGetMessagesForUser_noMessages() {
        // Arrange
        Long userId = 1L;
        when(messageRepository.findMessagesByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<MessageDTO> result = messageService.getMessagesForUser(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(messageRepository, times(1)).findMessagesByUserId(userId);
    }
}
