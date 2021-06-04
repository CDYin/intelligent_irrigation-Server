package com.example.demo.serviceImpl;

import com.example.demo.config.AutowiredUtils;
import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;
@Service
public class UserServiceimpl implements UserService {
    private UserDao userDao = AutowiredUtils.getBean(UserDao.class);

    @Override
    public User getUserByName(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public void setUserData(User user) {
        userDao.save(user);
    }
}
