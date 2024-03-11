package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.service.UserService;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.Mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yupi.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
* @author 卢端炜
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-03-02 13:42:12
*/
@Service
@Slf4j
 public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    @Resource
    private  UserMapper userMapper;
//    盐值
    private static final  String SALT = "yupi";
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
//1.校验
        if(StringUtils.isAnyBlank( userAccount, userPassword,checkPassword )){
            return -1;
        }
        if ( userAccount.length() < 4 ){
            return -1;
        }
        if(userPassword.length() < 8 || checkPassword.length() <8 ){
            return -1;
        }
// 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
//        账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            return -1;
        }
//        加密

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //        插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if(!saveResult){
            return  -1;
        }
        return user.getId() ;
    }

    @Override
    public User userlogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3. 用户脱敏
        User safetyUser = getSafeUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
        }
//        用户脱敏
    @Override
        public User getSafeUser(User originUser){
            if(originUser == null){
                return null;
            }
            User safeUser = new User();
            safeUser.setId(originUser.getId());
            safeUser.setUsername(originUser.getUsername());
            safeUser.setUserAccount(originUser.getUserAccount());
            safeUser.setAvatarUrl(originUser.getAvatarUrl());
            safeUser.setGender(originUser.getGender());
            safeUser.setPhone(originUser.getPhone());
            safeUser.setUserRole(originUser.getUserRole());
            safeUser.setEmail(originUser.getEmail());
            safeUser.setUserStatus(originUser.getUserStatus());
            safeUser.setCreateTime(originUser.getCreateTime());
            return  safeUser;
        }

    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}








