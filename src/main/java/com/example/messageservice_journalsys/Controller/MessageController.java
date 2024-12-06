package com.example.messageservice_journalsys.Controller;

import com.example.messageservice_journalsys.DTO.MessageDTO;
import com.example.messageservice_journalsys.Service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://kubernetes.docker.internal:30000")
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        MessageDTO sentMessage = messageService.sendMessage(messageDTO);
        return ResponseEntity.ok(sentMessage);
    }

    // Fetch all messages for a user
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<List<MessageDTO>> getConversationWithUser(@PathVariable Long userId) {
        List<MessageDTO> messages = messageService.getMessagesForUser(userId);
        return ResponseEntity.ok(messages);
    }
}
