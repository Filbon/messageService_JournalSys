package com.example.messageservice_journalsys.DTO;

import com.example.messageservice_journalsys.Model.Role;

public class UserDTO {

    private Long id;
    private String userName; // Username for display purposes
    private Role role; // Enum to identify user roles like DOCTOR, PATIENT, STAFF

    public UserDTO() {
    }

    // Constructor to initialize fields
    public UserDTO(Long id, String userName, Role role) {
        this.id = id;
        this.userName = userName;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
