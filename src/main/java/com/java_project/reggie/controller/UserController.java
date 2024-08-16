package com.java_project.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.java_project.reggie.Utiles.ValidateCodeUtils;
import com.java_project.reggie.common.R;
import com.java_project.reggie.entity.User;
import com.java_project.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/sendMsg")
    public R<String> getCode(@RequestBody User user, HttpSession httpSession){

        //获取手机号
        String phone = user.getPhone();

        //通过随机码模拟短信
        if(!StringUtils.isEmpty(phone)){
            //生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={"+code+"}");
            //保存到seesion里面
            httpSession.setAttribute(phone,code);
            return R.success("短信发送成功");
        }

        return R.error("短信发送失败");
    }

    //移动端用户登录
    @PostMapping("/login")
    //验证码的比对
    public R<User> login(@RequestBody Map map, HttpSession session){

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
       // String code = map.get("code").toString();

//        //从Session中获取保存的验证码
//        Object codeInSession = httpSession.getAttribute(phone);
//        //进行比对
//        if(codeInSession.equals(code)){
//
//
//
//        } //判断是否为新用户，是的话就存入
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //构造条件
        queryWrapper.eq(User::getPhone,phone);
        //开始查询
        User user  = userService.getOne(queryWrapper);
        //判断得到的结果是否为空


        if(user == null){
            //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        session.setAttribute("user",user.getId());
        return R.success(user);

    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

}
