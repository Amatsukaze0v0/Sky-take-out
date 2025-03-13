package com.skytakeout.interceptor;

import com.skytakeout.context.BaseContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从Session中获取用户ID
        Long empId = (Long) request.getSession().getAttribute("employee");

        // 将用户ID存储到ThreadLocal中
        if (empId != null) {
            BaseContext.setCurrentID(empId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束后清除ThreadLocal中的数据
        BaseContext.removeCurrentID();
    }
}
