package com.yupi.usercenter.service;
import java.util.Date;

import com.yupi.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
/*
* 用户服务测试
* */

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class UserServiceTest {
   @Resource
    private UserService userService;
   @Test
    public void textAddUser() {
       User user = new User();

       user.setUsername("ldw");
       user.setUserAccount("");
       user.setAvatarUrl("https://pic.rmb.bdstatic.com/bjh/914b8c0f9814b14c5fedeec7ec6615df5813.jpeg");
       user.setGender(0);
       user.setUserPassword("123");
       user.setPhone("123");
       user.setEmail("456");
       user.setUserStatus(0);
       user.setCreateTime("");
       user.setUpdateTime(new Date());
       user.setIsDelete(0);

       boolean result = userService.save(user);
       System.out.println(user.getId());
       Assertions.assertTrue(result);
   }


   @Test
   void userRegister() {
      String userAccount = "admin11";
      String userPassword = "12345678";
      String checkPassword = "12345678";
      long result = userService.userRegister(userAccount,userPassword,checkPassword);
   }
}