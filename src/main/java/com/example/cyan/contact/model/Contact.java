package com.example.cyan.contact.model;

import org.springframework.data.mongodb.core.mapping.Document;
import com.example.cyan.common.model.BaseDocument;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Document("contacts")
public class Contact extends BaseDocument {

    @NotBlank
    @Size(max = 100)
    private String customerName;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 30)
    private String phoneNumber;

    @NotBlank
    @Size(max = 200)
    private String subject;

    @NotBlank
    @Size(max = 5000)
    private String message;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
