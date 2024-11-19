package com.example.messageservice_journalsys.Service;

import com.example.messageservice_journalsys.DTO.MessageDTO;
import com.example.messageservice_journalsys.DTO.UserDTO;
import com.example.messageservice_journalsys.Model.Message;
import com.example.messageservice_journalsys.Repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserClient userClient;
    private final ModelMapper modelMapper;

    public MessageService(MessageRepository messageRepository, UserClient userClient, ModelMapper modelMapper) {
        this.messageRepository = messageRepository;
        this.userClient = userClient;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        // Fetch sender and recipient details using UserClient
        UserDTO sender = userClient.getUserById(messageDTO.getSenderId());
        UserDTO recipient = userClient.getUserById(messageDTO.getRecipientId());

        if (sender == null || recipient == null) {
            throw new EntityNotFoundException("Sender or recipient not found");
        }

        // Map MessageDTO to Message entity
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setSenderId(sender.getId());
        message.setRecipientId(recipient.getId());

        // Save message to the database
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
                    UserDTO sender = userClient.getUserById(message.getSenderId());
                    UserDTO recipient = userClient.getUserById(message.getRecipientId());
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
}
