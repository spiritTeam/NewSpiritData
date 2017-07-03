package com.spiritdata.passport.UGA.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.util.DigestUtils;

import com.spiritdata.GlobalConfig;
import com.spiritdata.framework.UGA.UgaUserService;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.passport.UGA.persis.po.UserPo;
import com.spiritdata.passport.login.persis.po.UserLoginPo;
import com.spiritdata.passport.session.redis.RedisUserDeviceKey;

public class UserService implements UgaUserService {
    @Resource(name="defaultDAO")
    private MybatisDAO<UserPo> userDao;
    @Resource(name="defaultDAO")
    private MybatisDAO<UserLoginPo> userLoginDao;
    @Resource(name="cConfig")
    private GlobalConfig gConfig;

    @PostConstruct
    public void initParam() {
        userDao.setNamespace("USER");
        userLoginDao.setNamespace("USERLOGIN");
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserPo getUserByLoginName(String loginName) {
        try {
            return userDao.getInfoObject("getUserByLoginName", loginName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserPo getUserById(String userId) {
        try {
            return userDao.getInfoObject("getUserById", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserPo getUserByPhoneNum(String mainPhoneNum) {
        try {
            return userDao.getInfoObject("getUserByPhoneNum", mainPhoneNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkPwd(UserPo u, String password) {
        String uEncodePwd=u.getPassword();
        if (StringUtils.isNullOrEmptyOrSpace(uEncodePwd)) return false;

        String EncodePwd=StringUtils.isNullOrEmptyOrSpace(u.getSalt())?password:u.getSalt()+password;
        EncodePwd=DigestUtils.md5DigestAsHex(EncodePwd.getBytes());

        return uEncodePwd.equals(EncodePwd);
    }

    public void saveUserLogin(RedisUserDeviceKey rUdk, int status) {
        UserLoginPo ulPo=new UserLoginPo();
        ulPo.setUlId(SequenceUUID.getUUIDSubSegment(4));
        ulPo.setSysType(rUdk.getSysWith().getOwnerType()+"");
        ulPo.setSysId(rUdk.getSysWith().getOwnerId());
        ulPo.setDeviceType(rUdk.getDevice().getDeviceTypeInt());
        ulPo.setDeviceId(rUdk.getDevice().getDeviceId());
        ulPo.setUserId(rUdk.getUserId());
        ulPo.setStatus(status);
        ulPo.setExpire(gConfig.getLoginsession());
        try {
            userLoginDao.insert(ulPo.toHashMap());
        } catch(Exception e) {
            try {
                userLoginDao.update("updateByBizKey", ulPo.toHashMap());
            } catch(Exception e1) {
                e1.printStackTrace();
            }
        }
        //把其他的用户使用情况设置为未登录
        userLoginDao.update("adjustOnLoginOk", ulPo.toHashMap());
    }
}