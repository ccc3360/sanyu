package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.R;
import com.example.common.ValidateCodeUtils;
import com.example.entity.User;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
//        获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
//        生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code={}",code);
//        调用阿里云提供的短信服务api完成发送短信
//        将生成的验证码存到session
//            session.setAttribute(phone,code);
            stringRedisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("");
        }
        return R.error("");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
//        获取手机号和验证码
        String phone=map.get("phone").toString();
        String code=map.get("code").toString();
        System.out.println(phone+code);
//        Object codeInSession = session.getAttribute(phone);
        Object codeInSession = stringRedisTemplate.opsForValue().get(phone);
        System.out.println(codeInSession);
        if(codeInSession!=null&&codeInSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User one = userService.getOne(queryWrapper);
            if (one==null){
                one=new User();
                one.setPhone(phone);
                one.setStatus(1);
                userService.save(one);
            }
            session.setAttribute("user",one.getId());
            stringRedisTemplate.delete(phone);
            return R.success(one);
        }
        return R.error("");
    }
}
