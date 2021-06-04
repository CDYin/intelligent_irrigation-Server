package com.example.demo.service;

import com.example.demo.entity.User;


public interface UserService {
    User getUserByName(String username);

    void setUserData(User user);
}
