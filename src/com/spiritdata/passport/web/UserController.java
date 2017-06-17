package com.spiritdata.passport.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.anotation.NeedLogin;

@Controller
@RequestMapping(value="/passport/user/")
public class UserController {
    @RequestMapping(value="aopLog.do")
    @ResponseBody
    public Map<String,Object> aopLog(HttpServletRequest request) {
        Map<String, Object> o=new HashMap<String, Object>();
        System.out.println("切片测试=================");
        o.put("AopTest", "切片测试");
        System.out.println("处理中::"+request.getAttribute("testBeforeDoing"));
        //throw new Exception("");
        return o;
    }

    /**
     * 用户注册
     * @param request
     * @return
     */
    @RequestMapping(value="login.do")
    @ResponseBody
    @NeedLogin
    public Map<String,Object> login(HttpServletRequest request) {
        Map<String, Object> o=new HashMap<String, Object>();
        System.out.println("切片测试=================");
        o.put("AopTest", "切片测试");
        System.out.println("处理中::"+request.getAttribute("testBeforeDoing"));
        //throw new Exception("");
        return o;
    }
    
}