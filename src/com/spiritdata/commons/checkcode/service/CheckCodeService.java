package com.spiritdata.commons.checkcode.service;

import java.util.Random;

import javax.annotation.Resource;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;

import com.spiritdata.commons.checkcode.model.BizCode;
import com.spiritdata.framework.ext.spring.redis.RedisOperService;
import com.spiritdata.framework.util.SpiritRandom;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.plugins.sms.ali.AliSmsMemory;
import com.spiritdata.plugins.sms.ali.AliSmsMessage;

@Service
public class CheckCodeService {
    @Resource(name="mainRedis")
    JedisConnectionFactory redisConn;

    /**
     * 生成验证码，并通过短信发给用户
     * @param bizStr 业务描述
     * @param userId 用户信息
     * @param phoneNum 用户手机号码
     * @throws InterruptedException 
     */
    public void generateCheckCode_SMS(BizCode bc, String userId, String phoneNum) throws InterruptedException {
        String checkCode=(SpiritRandom.getRandom(new Random(), 1000000, 1999999)+"").substring(1);
        //存入Redis
        String key=getKey(bc, userId, phoneNum);
        RedisOperService roService=new RedisOperService(redisConn, 3);
        try {
            roService.set(key, checkCode, 70*1000);
        } finally {
            roService.close();
            roService=null;
        }
        //发送短信
        AliSmsMemory asMem=AliSmsMemory.getInstance();
        AliSmsMessage asMsg=new AliSmsMessage();
        asMsg.setPhoneNum(phoneNum);
        asMsg.setParamJsonStr("{\"model\":\"["+bc.getName()+"]\", \"code\":\""+checkCode+"\"}");
        asMem.put2Queue(asMsg);

    }

    /**
     * 检查验证码是否争取
     * @param bc 业务描述
     * @param userId 用户信息
     * @param phoneNum 用户手机号码
     * @param checkCode 用户所传来的验证码
     * @return 1=检验成功；2=验证码不匹配；3=不存在相关的Key值
     */
    public int validateCheckCode_SMS(BizCode bc, String userId, String phoneNum, String checkCode) {
        String key=getKey(bc, userId, phoneNum);
        RedisOperService roService=new RedisOperService(redisConn, 3);
        try {
            String storeKey=roService.get(key);
            if (StringUtils.isNullOrEmptyOrSpace(storeKey)) return 3;
            if (!storeKey.equals(checkCode)) return 2;
        } finally {
            roService.close();
            roService=null;
        }
        return 1;
    }

    private String getKey(BizCode bc, String userId, String phoneNum) {
        if (bc==null||StringUtils.isNullOrEmptyOrSpace(userId)||StringUtils.isNullOrEmptyOrSpace(phoneNum)) 
            throw new IllegalArgumentException("参数不能为空");
        return "PhoneCHeck_"+bc.getCode()+"_CheckCode::UId_PhoneNum::"+userId+"_"+phoneNum;
    }
}