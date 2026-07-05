package com.aston.user.dto;

public class UserEventDto {

    private String email;
    private String operation;

    // Пустой конструктор
    public UserEventDto() {}

    // Конструктор со всеми полями
    public UserEventDto(String email, String operation) {
        this.email = email;
        this.operation = operation;
    }

    // Геттеры и сеттеры
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
}