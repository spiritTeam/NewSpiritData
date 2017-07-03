package com.spiritdata.passport.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spiritdata.anotation.ApiName;
import com.spiritdata.commons.model.UserDeviceKey;
import com.spiritdata.framework.core.lock.BlockLockConfig;
import com.spiritdata.framework.core.lock.ExpirableBlockKey;
import com.spiritdata.framework.ext.redis.lock.RedisBlockLock;
import com.spiritdata.framework.ext.spring.redis.RedisOperService;
import com.spiritdata.framework.util.SpiritRandom;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.passport.UGA.persis.po.UserPo;
import com.spiritdata.passport.UGA.service.UserService;
import com.spiritdata.passport.session.SessionService;
import com.spiritdata.passport.session.redis.RedisUserDeviceKey;
import com.spiritdata.plugins.sms.ali.AliSMS;

@Controller
@RequestMapping(value="/passport/user/")
public class UserController {
    @Resource(name="mainRedis")
    JedisConnectionFactory redisConn;
    @Resource
    private UserService userService;
    @Resource(name="redisSessionService")
    private SessionService sessionService;

    /**
     * 用户登录
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value="login.do")
    @ResponseBody
    @ApiName("2.1.1-个人用户登录")
    public Map<String,Object> login(HttpServletRequest request) throws Exception {
        Map<String, Object> retM=new HashMap<String, Object>();
        //获得参数
        Map<String, Object> m=(Map<String, Object>)request.getAttribute("mergedParam");
        if (m==null||m.isEmpty()) throw new Exception("参数为空，无法处理");

        String tempStr=null;
        //一、获取参数
        //1-登录类型——是否使用验证图片，是否使用手机验证码。
        //  登录类型：1-仅用户名密码，这是默认值；2-手机验证码；3-验证码；4同时用2|3;
        int loginType=1;
        tempStr=m.get("LoginType")==null?null:m.get("LoginType")+"";
        try { loginType=Integer.parseInt(tempStr); } catch(Exception e) {};
        //2-是否使用密码
        int usePwd=1;
        tempStr=m.get("UsePwd")==null?null:m.get("UsePwd")+"";
        try { usePwd=Integer.parseInt(tempStr); } catch(Exception e) {};
        if (loginType==1||loginType==3) usePwd=1;
        //3-登录名称：手机，登录名，唯一好都可以处理
        String userName=m.get("UserName")==null?null:m.get("UserName")+"";
        //4-密码
        String password=m.get("Password")==null?null:m.get("Password")+"";
        //5-验证串：验证图片所对应的串，当LoginType=3|4时，必须
        String checkStr=m.get("CheckStr")==null?null:m.get("CheckStr")+"";
        //6-短信验证码：验证图片所对应的串，当LoginType=3|4时，必须
        String checkNum=m.get("PhoneCheckNum")==null?null:m.get("PhoneCheckNum")+"";

        //二、检查参数
        if (StringUtils.isNullOrEmptyOrSpace(userName)) {
            retM.put("ReturnType", "2100");
            retM.put("Message", "用户名为空");
            return retM;
        }
        if (usePwd==1&&StringUtils.isNullOrEmptyOrSpace(password)) {
            retM.put("ReturnType", "2110");
            retM.put("Message", "密码为空");
            return retM;
        }
        if ((loginType==2||loginType==4)&&StringUtils.isNullOrEmptyOrSpace(checkStr)) {
            retM.put("ReturnType", "2111");
            retM.put("Message", "验证串为空");
            return retM;
        }
        if ((loginType==3||loginType==4)&&StringUtils.isNullOrEmptyOrSpace(checkNum)) {
            retM.put("ReturnType", "2112");
            retM.put("Message", "手机验证码为空");
            return retM;
        }

        //三、处理验证过程
        UserPo u=userService.getUserByLoginName(userName);
        if (u==null) u=userService.getUserByPhoneNum(userName);
        //1-判断是否存在用户
        if (u==null) { //无用户
            retM.put("ReturnType", "1002");
            retM.put("Message", "无登录名为["+userName+"]的用户");
            return retM;
        }
        //2-判断密码是否正确
        if (usePwd==1&&!userService.checkPwd(u, password)) {
            retM.put("ReturnType", "1100");
            retM.put("Message", "密码不匹配");
            return retM;
        }
        //3-判断验证串是否正确
        if (loginType==2||loginType==4) {
            retM.put("ReturnType", "2111");
            retM.put("Message", "验证串为空");
            return retM;
        }
        //4-判断手机验证码
        if (loginType==3||loginType==4) {
            retM.put("ReturnType", "2111");
            retM.put("Message", "验证串为空");
            return retM;
        }

        //四、登录成功后，写相关的Session信息
        try {
            //上锁
            UserDeviceKey udk=(UserDeviceKey)request.getAttribute("udKey");
            RedisUserDeviceKey rUdk=new RedisUserDeviceKey(udk);
            RedisOperService roService=new RedisOperService(redisConn, 0);
            ExpirableBlockKey rLock=RedisBlockLock.lock(rUdk.getKey_LockLogin(), roService, new BlockLockConfig(5, 2, 0, 50));
            try {
                userService.saveUserLogin(rUdk, 1);
                sessionService.registUser(rUdk, "2.1.1-个人用户登录");
            } finally {
                rLock.unlock();
                roService.close();
                roService=null;
            }
            retM.put("ReturnType", "1001");
            retM.put("Message", "登录成功");
        } catch(Exception e) {
            retM.put("ReturnType", "T");
            retM.put("TClass", e.getClass().getName());
            retM.put("Message", e.getMessage());
        }

        return retM;
    }
    
    /**
     * 用户注销
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value="logout.do")
    @ResponseBody
    @ApiName("2.1.2-个人用户注销")
    public Map<String,Object> logout(HttpServletRequest request) throws Exception {
        Map<String, Object> retM=new HashMap<String, Object>();
        //获得参数
        Map<String, Object> m=(Map<String, Object>)request.getAttribute("mergedParam");
        if (m==null||m.isEmpty()) throw new Exception("参数为空，无法处理");

        AliSMS aliSms=new AliSMS();
        int random=SpiritRandom.getRandom(new Random(), 1000000, 1999999);
        String checkNum1=(random+"").substring(1);
        aliSms.sendSms("13910672205", checkNum1, "测试");

        String tempStr=null;
        //一、获取参数
        //1-登录类型——是否使用验证图片，是否使用手机验证码。
        //  登录类型：1-仅用户名密码，这是默认值；2-手机验证码；3-验证码；4同时用2|3;
        int loginType=1;
        tempStr=m.get("LoginType")==null?null:m.get("LoginType")+"";
        try { loginType=Integer.parseInt(tempStr); } catch(Exception e) {};
        //2-是否使用密码
        int usePwd=1;
        tempStr=m.get("UsePwd")==null?null:m.get("UsePwd")+"";
        try { usePwd=Integer.parseInt(tempStr); } catch(Exception e) {};
        if (loginType==1||loginType==3) usePwd=1;
        //3-登录名称：手机，登录名，唯一好都可以处理
        String userName=m.get("UserName")==null?null:m.get("UserName")+"";
        //4-密码
        String password=m.get("Password")==null?null:m.get("Password")+"";
        //5-验证串：验证图片所对应的串，当LoginType=3|4时，必须
        String checkStr=m.get("CheckStr")==null?null:m.get("CheckStr")+"";
        //6-短信验证码：验证图片所对应的串，当LoginType=3|4时，必须
        String checkNum=m.get("PhoneCheckNum")==null?null:m.get("PhoneCheckNum")+"";

        //二、检查参数
        if (StringUtils.isNullOrEmptyOrSpace(userName)) {
            retM.put("ReturnType", "2100");
            retM.put("Message", "用户名为空");
            return retM;
        }
        if (usePwd==1&&StringUtils.isNullOrEmptyOrSpace(password)) {
            retM.put("ReturnType", "2110");
            retM.put("Message", "密码为空");
            return retM;
        }
        if ((loginType==2||loginType==4)&&StringUtils.isNullOrEmptyOrSpace(checkStr)) {
            retM.put("ReturnType", "2111");
            retM.put("Message", "验证串为空");
            return retM;
        }
        if ((loginType==3||loginType==4)&&StringUtils.isNullOrEmptyOrSpace(checkNum)) {
            retM.put("ReturnType", "2112");
            retM.put("Message", "手机验证码为空");
            return retM;
        }

        //三、处理验证过程
        UserPo u=userService.getUserByLoginName(userName);
        if (u==null) u=userService.getUserByPhoneNum(userName);
        //1-判断是否存在用户
        if (u==null) { //无用户
            retM.put("ReturnType", "1002");
            retM.put("Message", "无登录名为["+userName+"]的用户");
            return retM;
        }
        //2-判断密码是否正确
        if (usePwd==1&&!userService.checkPwd(u, password)) {
            retM.put("ReturnType", "1100");
            retM.put("Message", "密码不匹配");
            return retM;
        }
        //3-判断验证串是否正确
        if (loginType==2||loginType==4) {
            retM.put("ReturnType", "2111");
            retM.put("Message", "验证串为空");
            return retM;
        }
        //4-判断手机验证码
        if (loginType==3||loginType==4) {
            retM.put("ReturnType", "2111");
            retM.put("Message", "验证串为空");
            return retM;
        }

        //四、登录成功后，写相关的Session信息
        try {
            //上锁
            UserDeviceKey udk=(UserDeviceKey)request.getAttribute("udKey");
            RedisUserDeviceKey rUdk=new RedisUserDeviceKey(udk);
            RedisOperService roService=new RedisOperService(redisConn, 0);
            ExpirableBlockKey rLock=RedisBlockLock.lock(rUdk.getKey_LockLogin(), roService, new BlockLockConfig(5, 2, 0, 50));
            try {
                userService.saveUserLogin(rUdk, 1);
                sessionService.registUser(rUdk, "2.1.1-个人用户登录");
            } finally {
                rLock.unlock();
                roService.close();
                roService=null;
            }
            retM.put("ReturnType", "1001");
            retM.put("Message", "登录成功");
        } catch(Exception e) {
            retM.put("ReturnType", "T");
            retM.put("TClass", e.getClass().getName());
            retM.put("Message", e.getMessage());
        }

        return retM;
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