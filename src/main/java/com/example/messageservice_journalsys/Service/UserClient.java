package com.example.messageservice_journalsys.Service;

import com.example.messageservice_journalsys.DTO.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api/auth").build();
    }

    public UserDTO getUserById(Long userId) {
        try {
            return webClient.get()
                    .uri("/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .block();
        } catch (Exception e) {
            return null; // Handle errors gracefully
        }
    }

}

