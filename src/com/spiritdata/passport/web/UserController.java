package com.spiritdata.passport.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.anotation.ApiName;
import com.spiritdata.anotation.NeedLogin;

@Controller
@RequestMapping(value="/passport/user/")
public class UserController {
    /**
     * 用户注册
     * @param request
     * @return
     * @throws Exception 
     */
    @RequestMapping(value="login.do")
    @ResponseBody
    @NeedLogin
    @ApiName("2.1.1-个人用户登录")
    public Map<String,Object> login(HttpServletRequest request) throws Exception {
        Map<String, Object> o=new HashMap<String, Object>();
        System.out.println("切片测试=================");
        o.put("AopTest", "切片测试");
        System.out.println("处理中::"+request.getAttribute("testBeforeDoing"));
        //throw new Exception("");
        List<String> l=new ArrayList<String>();
        l.get(2);
        
        if (o.size()>0) throw new Exception("仅仅是个测试");
        return o;
    }
    
    /**
     * 用户注册
     * @param request
     * @return
     * @throws Exception 
     */
    @RequestMapping(value="register.do")
    @ResponseBody
    public Map<String,Object> register(HttpServletRequest request) {
        Map<String, Object> o=new HashMap<String, Object>();
        System.out.println("切片测试=================");
        o.put("AopTest", "切片测试");
        System.out.println("处理中::"+request.getAttribute("testBeforeDoing"));
        if (o.size()>0) throw new RuntimeException("仅仅是个测试，测试异常情况");
        return o;
    }
}