package com.example.taskReminder.entity;

public class TestUser implements UserInf {
    private Long userId;
    private String username;

    public TestUser(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public String getUsername() {
        return username;
    }
}

