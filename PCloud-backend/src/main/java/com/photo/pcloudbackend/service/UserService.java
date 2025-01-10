package com.photo.pcloudbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.photo.pcloudbackend.model.dto.user.UserQueryRequest;
import com.photo.pcloudbackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.photo.pcloudbackend.model.vo.LoginUserVo;
import com.photo.pcloudbackend.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author CMH
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-01-09 16:18:58
*/
public interface UserService extends IService<User> {
    long userRegister(String userAccount, String userPassword,String checkPassword);
    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
    /**
     * 获取登录用户信息
     * @param user
     * @return
     */
    LoginUserVo getLoginUserVo(User user);
    /**
     * 获取脱敏后的用户信息
     */
    UserVo getUserVo(User user);
    /**
     * 获取脱敏后的用户信息列表
     */
    List<UserVo> getUserVoList(List<User> userList);
    /**
     * 获取查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
    /**
     * 用户注销登录
     * @param request
     */
    boolean userLogout(HttpServletRequest request);
}
