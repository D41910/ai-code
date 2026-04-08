package com.dsj.aicode.aop;

import com.dsj.aicode.Exception.BusinessException;
import com.dsj.aicode.Exception.ErrorCode;
import com.dsj.aicode.annotation.AuthCheck;
import com.dsj.aicode.model.entity.User;
import com.dsj.aicode.model.enums.UserRoleEnum;
import com.dsj.aicode.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author dongsijun
 * @date 2026/4/2  01:02
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截 添加AuthCheck注解就必须登陆 设置值为管理员 就必须为管理员
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        //当前登陆用户
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getByCode(mustRole);
        //不需要权限放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        UserRoleEnum userRoleEnum = UserRoleEnum.getByValue(loginUser.getUserRole());
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if(UserRoleEnum.ADMIN == mustRoleEnum && UserRoleEnum.ADMIN != userRoleEnum) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
