package com.photo.pcloudbackend.aop;

import com.photo.pcloudbackend.annotation.AuthCheck;
import com.photo.pcloudbackend.exception.BusinessException;
import com.photo.pcloudbackend.exception.ErrorCode;
import com.photo.pcloudbackend.model.entity.User;
import com.photo.pcloudbackend.model.enums.UserRoleEnum;
import com.photo.pcloudbackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//切面
@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;
    //切点执行拦截
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint pjp, AuthCheck authCheck) throws Throwable {
        //获取注解中的权限
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //如果不需要权限，放行
        if(mustRoleEnum == null){
            return pjp.proceed();
        }
        //必须要有权限
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if(userRoleEnum == null){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "用户没有权限");
        }
        //要求必须有管理员权限，但是用户只有管理员权限，拒绝
        if(userRoleEnum.ADMIN.equals(mustRoleEnum)&&!userRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "用户没有权限");
        }
        //有权限，放行
        return pjp.proceed();
    }
}
