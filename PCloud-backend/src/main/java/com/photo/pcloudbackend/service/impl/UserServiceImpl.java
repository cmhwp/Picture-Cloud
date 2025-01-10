package com.photo.pcloudbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photo.pcloudbackend.constant.UserConstant;
import com.photo.pcloudbackend.exception.BusinessException;
import com.photo.pcloudbackend.exception.ErrorCode;
import com.photo.pcloudbackend.model.dto.user.UserQueryRequest;
import com.photo.pcloudbackend.model.entity.User;
import com.photo.pcloudbackend.model.enums.UserRoleEnum;
import com.photo.pcloudbackend.model.vo.LoginUserVo;
import com.photo.pcloudbackend.model.vo.UserVo;
import com.photo.pcloudbackend.service.UserService;
import com.photo.pcloudbackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.dao.DuplicateKeyException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author CMH
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-01-09 16:18:58
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验参数
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"用户账号过短");
        }
        if(userPassword.length()<8 || checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"用户密码过短");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"两次密码不一致");
        }
        //2.判断账号是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.count(queryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"账号已被注册");
        }
        //3.加密密码
        String encryptPassword = getEncryptPassword(userPassword);
        //4.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("user"+userAccount);
        //用户默认权限
        user.setUserRole(UserRoleEnum.USER.getValue());
        try {
            boolean saveResult = this.save(user);
            if(!saveResult){
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,"用户注册失败");
            }
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "账号已被注册");
        }
        return user.getId();
    }

    /**
     * 获取加密后的密码
     * @param userPassword
     * @return
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        //加盐
        String salt = "photo";
        return DigestUtils.md5DigestAsHex((salt+userPassword).getBytes());
    }

    @Override
    public LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"账号或者密码为空，请检查后重试");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"用户账号错误");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"用户密码错误");
        }
        //2.对用户传递的密码进行加密
        String encryptPassword = getEncryptPassword(userPassword);
        //3.查询用户信息在数据库中是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        System.out.println(user);
        //不存在抛出异常
        if(user==null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"用户不存在或者密码错误");
        }
        //4.保存用户登录状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
        return this.getLoginUserVo(user);
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser==null||currentUser.getId()==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户未登录");
        }
        //从数据查询
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if(currentUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"用户不存在");
        }
        return currentUser;
    }

    /**
     * 获取登录用户视图
     * @param user
     * @return
     */
    @Override
    public LoginUserVo getLoginUserVo(User user) {
        //用户为空
        if (user == null) {
            return null;
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtil.copyProperties(user,loginUserVo);
        return loginUserVo;
    }

    /**
     * 获取用户视图
     * @param user
     * @return
     */
    @Override
    public UserVo getUserVo(User user) {
        //用户为空
        if (user == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user,userVo);
        return userVo;
    }

    /**
     * 获取用户视图列表
     * @param userList
     * @return
     */
    @Override
    public List<UserVo> getUserVoList(List<User> userList) {
        if(CollUtil.isEmpty(userList)){
            //为空返回空列表
            return new ArrayList<>();
        }

        return userList.stream().map(this::getUserVo).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"参数为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        queryWrapper.eq(ObjectUtil.isNotNull(id),"id",id);
        queryWrapper.eq(StrUtil.isNotBlank(userAccount),"userAccount",userAccount);
        queryWrapper.eq(StrUtil.isNotBlank(userName),"userName",userName);
        queryWrapper.eq(StrUtil.isNotBlank(userProfile),"userProfile",userProfile);
        queryWrapper.eq(StrUtil.isNotBlank(userRole),"userRole",userRole);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField),sortOrder.equals("ascend"),sortField);
        return queryWrapper;
    }

    /**
     * 用户注销登录
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser==null||currentUser.getId()==null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"用户未登录");
        }
        //移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }
}




