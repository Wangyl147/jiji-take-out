package org.wangyl.jiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wangyl.jiji.common.R;

import org.wangyl.jiji.common.ValidateCodeUtils;
import org.wangyl.jiji.entity.User;
import org.wangyl.jiji.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//用户管理
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //注入redisTemplate对象，用于操作Redis
    @Autowired
    private StringRedisTemplate redisTemplate;

    //发送验证码
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phoneNumber = user.getPhone();
        if(StringUtils.isNotEmpty(phoneNumber)){
            //生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码：{}",code);
            //缓存验证码，并设置有效期5分钟
            redisTemplate.opsForValue().set(phoneNumber,code,5, TimeUnit.MINUTES);
            //调用api
            //SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phoneNumber,code);
            //保存验证码到session
            //session.setAttribute(phoneNumber,code);
            return R.success("发送验证码成功");
        }
        return R.error("发送验证码失败");

    }

    //用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从缓存中取出验证码并比对
        String sessionCode = redisTemplate.opsForValue().get(phone);

        //从session中获取和比对
        //Object sessionCode = session.getAttribute(phone);
        if(sessionCode!=null && sessionCode.equals(code)) {
            //判断当前手机号用户是否存在
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(lambdaQueryWrapper);
            if (user == null) {
                //若为新用户则自动注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            //如果用户登录成功，删除redis中的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }

        return R.error("登录失败");
    }

    //用户登出
    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("user");
        return R.success("");
    }

}
