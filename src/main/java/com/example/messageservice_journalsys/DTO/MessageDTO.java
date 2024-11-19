package com.example.messageservice_journalsys.DTO;

import java.time.LocalDateTime;

public class MessageDTO {
    private Long id;
    private Long senderId;         // Sender's ID
    private Long recipientId;      // Recipient's ID
    private String senderName;
    private String recipientName;  // Renamed for consistency
    private String content;
    private LocalDateTime sentDate;

    // Default constructor for easier instantiation
    public MessageDTO() {}

    public MessageDTO(Long id, Long senderId, Long recipientId, String senderName, String recipientName,
                      String content, LocalDateTime sentDate) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.content = content;
        this.sentDate = sentDate;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

}