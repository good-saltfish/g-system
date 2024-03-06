package com.yupi.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.request.UserLoginRequest;
import com.yupi.usercenter.model.domain.request.UserRegisterRequest;
import com.yupi.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yupi.usercenter.contant.UserConstant.*;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {
     @Resource
     private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
         if(userRegisterRequest == null ){
             return  null;
         }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            return  null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);

    }
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request ){
        if(userLoginRequest == null ){
            return  null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword )){
            return  null;
        }
        return userService.userlogin(userAccount, userPassword, request );

    }
    @GetMapping("/search")
    public List<User> searchUsers(String username,HttpServletRequest request ) {
        if(!isAdmin(request)){
            return  new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)) {
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        return userList.stream().map(user -> {
            user.setUserPassword(null);
            return  userService.getSafeUser(user);
        }).collect(Collectors.toList());
    }
    @PostMapping("/delete")
    public boolean deleteUsers(@RequestBody long id,HttpServletRequest request ) {
        if(!isAdmin(request)){
            return  false;
        }
        if(id<=0) {
            return  false;
        }
        return  userService.removeById(id);
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){
        //        仅管理员可见
        Object userobj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userobj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
