package com.yupi.usercenter.service;

import com.yupi.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 卢端炜
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-03-02 13:42:12
*/
public interface UserService extends IService<User> {
    /**
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
   long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     * 登入
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后用户信息
     */
   User userlogin(String userAccount, String userPassword, HttpServletRequest request);

    //        用户脱敏
    User getSafeUser(User originUser);
    int userLogout(HttpServletRequest request);
}
