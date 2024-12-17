package com.example.messageservice_journalsys.Service;

import com.example.messageservice_journalsys.DTO.MessageDTO;
import com.example.messageservice_journalsys.DTO.UserDTO;
import com.example.messageservice_journalsys.Model.Message;
import com.example.messageservice_journalsys.Repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;

    public MessageService(MessageRepository messageRepository, RestTemplate restTemplate, ModelMapper modelMapper) {
        this.messageRepository = messageRepository;
        this.restTemplate = restTemplate;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        // Fetch sender details
        UserDTO sender = getUserById(messageDTO.getSenderId());
        if (sender == null) {
            throw new EntityNotFoundException("Sender not found with ID: " + messageDTO.getSenderId());
        }

        // Fetch recipient details
        UserDTO recipient = getUserById(messageDTO.getRecipientId());
        if (recipient == null) {
            throw new EntityNotFoundException("Recipient not found with ID: " + messageDTO.getRecipientId());
        }

        // Map MessageDTO to Message entity
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setSenderId(sender.getId());
        message.setRecipientId(recipient.getId());

        // Save the message to the database
        Message savedMessage = messageRepository.save(message);

        // Convert saved Message entity back to MessageDTO
        MessageDTO sentMessageDTO = modelMapper.map(savedMessage, MessageDTO.class);
        sentMessageDTO.setSenderName(sender.getUserName());
        sentMessageDTO.setRecipientName(recipient.getUserName());
        return sentMessageDTO;
    }

    public List<MessageDTO> getMessagesForUser(Long userId) {
        // Retrieve messages where the user is either the sender or the recipient
        List<Message> messages = messageRepository.findMessagesByUserId(userId);

        // Convert messages to DTOs and enrich with sender/recipient names
        return messages.stream()
                .map(message -> {
                    UserDTO sender = getUserById(message.getSenderId());
                    UserDTO recipient = getUserById(message.getRecipientId());
                    return new MessageDTO(
                            message.getId(),
                            message.getSenderId(),
                            message.getRecipientId(),
                            sender != null ? sender.getUserName() : "Unknown",
                            recipient != null ? recipient.getUserName() : "Unknown",
                            message.getContent(),
                            message.getTimestamp()
                    );
                })
                .collect(Collectors.toList());
    }

    // Utility method to fetch user details using RestTemplate
    private UserDTO getUserById(Long userId) {
        try {
            String userApiUrl = "https://userservice.app.cloud.cbh.kth.se/api/user/users/{id}";
            ResponseEntity<UserDTO> userResponse = restTemplate.exchange(userApiUrl, HttpMethod.GET, null, UserDTO.class, userId);
            if(userResponse.getStatusCode() == HttpStatus.OK) {
                return userResponse.getBody();
            }
        } catch (HttpClientErrorException.NotFound e) {
            // Handle user not found error
            return null;
        } catch (Exception e) {
            // Log and rethrow unexpected exceptions
            throw new RuntimeException("Error fetching user details for ID: " + userId, e);
        }
        return null;
    }
}


