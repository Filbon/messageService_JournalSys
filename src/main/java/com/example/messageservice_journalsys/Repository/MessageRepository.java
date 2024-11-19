package com.example.messageservice_journalsys.Repository;

import com.example.messageservice_journalsys.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.senderId = :userId OR m.recipientId = :userId")
    List<Message> findMessagesByUserId(@Param("userId") Long userId);
}
