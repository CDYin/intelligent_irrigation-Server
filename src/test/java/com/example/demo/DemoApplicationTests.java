package com.example.demo;

import com.example.demo.config.AutowiredUtils;
import com.example.demo.entity.User;
import com.example.demo.service.RecordService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class DemoApplicationTests {

    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    public void findByName(){
        User user = userService.getUserByName("13752172651");
//        User user = new User();
//        user.setUserData("111111","1111111");
        userService.setUserData(user);
        System.out.println(user.getPassword());
    }
}
