package com.photo.pcloudbackend.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photo.pcloudbackend.annotation.AuthCheck;
import com.photo.pcloudbackend.common.BaseResponse;
import com.photo.pcloudbackend.common.DeleteRequest;
import com.photo.pcloudbackend.common.PageRequest;
import com.photo.pcloudbackend.common.ResultUtils;
import com.photo.pcloudbackend.constant.UserConstant;
import com.photo.pcloudbackend.exception.BusinessException;
import com.photo.pcloudbackend.exception.ErrorCode;
import com.photo.pcloudbackend.exception.ThrowUtils;
import com.photo.pcloudbackend.model.dto.user.*;
import com.photo.pcloudbackend.model.entity.User;
import com.photo.pcloudbackend.model.vo.LoginUserVo;
import com.photo.pcloudbackend.model.vo.UserVo;
import com.photo.pcloudbackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMETER_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMETER_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        System.out.println("33"+userAccount + userPassword);
        LoginUserVo loginUserVo = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVo);
    }
    /**
     * 获取登录用户信息
     */
    @GetMapping("/get/loginUser")
    public BaseResponse<LoginUserVo> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        LoginUserVo loginUserVo = userService.getLoginUserVo(loginUser);
        return ResultUtils.success(loginUserVo);
    }
    /**
     * 用户注销登录
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }
    /**
     * 创建用户
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMETER_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        //默认密码
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        //默认头像
        user.setUserAvatar("https://cdn.acwing.com/media/user/profile/photo/112320_lg_bb71911b53.png");
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }
    /**
     * 根据ID获取用户（管理员）
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/get")
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMETER_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }
    /**
     * 根据ID获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVo> getUserVoById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMETER_ERROR);
        BaseResponse<User> userResponse = getUserById(id);
        User user = userResponse.getData();
        UserVo userVo = userService.getUserVo(user);
        return ResultUtils.success(userVo);
    }
    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null||deleteRequest.getId()<=0, ErrorCode.PARAMETER_ERROR);
        boolean result = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }
    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if(userUpdateRequest == null||userUpdateRequest.getId()==null){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        Boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }
    /**
     * 分页查询
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVo>> listUserVoByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMETER_ERROR);
        long currentPageNum = userQueryRequest.getCurrentPageNum();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(currentPageNum, pageSize), userService.getQueryWrapper(userQueryRequest));
        Page<UserVo> userVoPage = new Page<>(currentPageNum, pageSize,userPage.getTotal());
        List<UserVo> userVoList = userService.getUserVoList(userPage.getRecords());
        return ResultUtils.success(userVoPage.setRecords(userVoList));
    }
}
