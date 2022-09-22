package com.kai.waimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kai.waimai.entity.R;
import com.kai.waimai.entity.User;
import com.kai.waimai.service.UserService;
import com.kai.waimai.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private UserService service;


    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            //未接入 email   tongguoemail通知客户端
            System.out.println(code);
            log.info("验证码是：{}",code);

            //需要将生成的验证码保存到session
//            session.setAttribute(phone,code);

            //将验证码保存到redis,并且设置有效期
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);


            return R.success("短信发送成功"+code);

        }



        return null;
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info(map.toString());
        
        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code =map.get("code").toString();

        //从session中获取保存的验证码
//        Object attribute = session.getAttribute(phone);

        //从redis中获取缓存的验证码
        Object attribute = redisTemplate.opsForValue().get(phone);


        if(attribute!=null && attribute.equals(code)){
            //判断成功再判断用户是否新用户
            LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<>();

            wrapper.eq(User::getPhone,phone);

            User one = service.getOne(wrapper);
            if(one==null){
                one=new User();
                one.setPhone(phone);
                one.setStatus(1);
                service.save(one);
            }
            session.setAttribute("user",one.getId());
            //用户登陆成功删除缓存的验证码
            redisTemplate.delete(phone);
            return R.success(one);

        }
        return R.error("短信发送失败");
    }







}

