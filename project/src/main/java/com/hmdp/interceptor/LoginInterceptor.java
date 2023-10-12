package com.hmdp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1. 获取session
        HttpSession session = request.getSession();
        //2. session中得到用户
        UserDTO user = (UserDTO) session.getAttribute("user");
        //3. 判断用户是否存在
        if (user == null) {
            // 3.2. 不存在，进行拦截，返回401状态码
            response.setStatus(401);
            return false;
        }
        //3.1. 存在保存至ThreadLocal中，并且放行
//        UserDTO userDTO = new UserDTO();
//        BeanUtil.copyProperties(user, userDTO);
        UserHolder.saveUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
       //移除用户
        UserHolder.removeUser();
    }
}
