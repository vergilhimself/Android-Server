package com.example.library;

public class UserManager {
    private static UserManager instance;
    private String username;
    private String email;

    // Приватный конструктор для предотвращения создания экземпляров
    private UserManager() {}

    // Метод для получения единственного экземпляра класса
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // Методы для установки и получения имени пользователя
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    // Методы для установки и получения email
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}