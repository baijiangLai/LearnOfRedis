package com.hmdp.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.UserDTO;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RefreshInterceptor implements HandlerInterceptor {

    private StringRedisTemplate redisTemplate;

    public RefreshInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        //1. 获取session
//        HttpSession session = request.getSession();
//
//        //2. session中得到用户
//        UserDTO user = (UserDTO) session.getAttribute("user");
//        //3. 判断用户是否存在
//        if (user == null) {
//            // 3.2. 不存在，进行拦截，返回401状态码
//            response.setStatus(401);
//            return false;
//        }
//        //3.1. 存在保存至ThreadLocal中，并且放行
////        UserDTO userDTO = new UserDTO();
////        BeanUtil.copyProperties(user, userDTO);
//        UserHolder.saveUser(user);

        String token = request.getHeader("authorization");
        if (StrUtil.isEmpty(token)) {
//            response.setStatus(401);
            return true;
        }
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(RedisConstants.LOGIN_USER_KEY + token);
        if (userMap.isEmpty()) {
//            response.setStatus(401);
            return true;
        }

        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

        UserHolder.saveUser(userDTO);

        redisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token, RedisConstants.LOGIN_USER_TTL, TimeUnit.SECONDS);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
       //移除用户
        UserHolder.removeUser();
    }
}
