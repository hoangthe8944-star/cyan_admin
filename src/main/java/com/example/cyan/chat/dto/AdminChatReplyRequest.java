package com.example.cyan.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminChatReplyRequest {

    @NotBlank
    @Size(max = 120)
    private String adminName;

    @NotBlank
    @Size(max = 2000)
    private String message;

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
