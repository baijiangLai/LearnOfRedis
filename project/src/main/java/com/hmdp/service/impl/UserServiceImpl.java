package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1. 校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号码格式错误");
        }
        // 2. 合格生成验证码， 不合格直接返回
        String code = RandomUtil.randomNumbers(6);

        // 3. 将验证码保存到session
//        session.setAttribute("code", code);
//        stringRedisTemplate.opsForValue().set();
        // 3. 将验证码保存至redis中

        // 4. 返回验证码
        log.info("发送验证码成功，验证码为: {}", code);
        //返回
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        //1. 校验验证码是否一致，不一致直接返回
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号码格式错误");
        }
        String code = loginForm.getCode();
        String sessionCode = (String) session.getAttribute("code");
        if (sessionCode == null || !sessionCode.equals(code)) {
            return Result.fail("验证码不正确");
        }
        //2. 一致，先查询手机号码是否存在库
        User user = query().eq("phone", phone).one();
        //2.1. 库里没有，创建用户，将用户保存到库里，将用户保存至session
        if (user == null) {
            user = createUserByPhone(phone);
        }
        //2.2. 库里有，那么将用户信息保存至session中
        session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));
        return Result.ok();
    }

    /**
     * 通过输入的手机号创建用户
     * @param phone
     * @return
     */
    private User createUserByPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomNumbers(10));
        save(user);
        return null;
    }
}
