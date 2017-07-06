package com.spiritdata.passport.session.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;

import com.spiritdata.GlobalConfig;
import com.spiritdata.commons.model.DeviceType;
import com.spiritdata.commons.model.UserDeviceKey;
//import com.spiritdata.framework.core.lock.BlockLockConfig;
//import com.spiritdata.framework.core.lock.ExpirableBlockKey;
//import com.spiritdata.framework.ext.redis.lock.RedisBlockLock;
import com.spiritdata.framework.ext.spring.redis.RedisOperService;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.passport.UGA.persis.po.UserPo;
import com.spiritdata.passport.UGA.rcache.GetUser;
import com.spiritdata.passport.UGA.service.UserService;
import com.spiritdata.passport.login.persis.po.UserLoginPo;
import com.spiritdata.passport.login.service.UserLoginService;
import com.spiritdata.passport.session.SessionService;
import com.spiritdata.passport.session.redis.RedisUserDeviceKey;

@Service
public class RedisSessionService implements SessionService {
    @Resource(name="mainRedis")
    JedisConnectionFactory redisConn;
    @Resource
    private UserService userService;
    @Resource
    private UserLoginService ulService;
    @Resource(name="gConfig")
    private GlobalConfig gConfig;

    @Override
    public Map<String, Object> getRealLoginUdk(UserDeviceKey udk) {
        Map<String, Object> map=checkUdk(udk);
        if (map!=null) return map;
        map=new HashMap<String, Object>();

        RedisUserDeviceKey rUdk=new RedisUserDeviceKey(udk);
        RedisOperService roService=new RedisOperService(redisConn, 2);
        String _value=roService.get(rUdk.getKey_SysDevice_User());
        String _userId=(_value==null?null:new String(_value));
        if (_userId==null) {
            map.put("ReturnType", "1011");
        } else {
            map.put("ReturnType", "1001");
            udk.setUserId(_userId.substring(_userId.indexOf('_')+1));
            map.put("UDK", udk);
        }
        return map;
    }

    @Override
    public Map<String, Object> dealUDkeyEntry(UserDeviceKey udk, String operDesc) {
        Map<String,Object> map=checkUdk(udk);
        if (map!=null) return map;
        map=new HashMap<String, Object>();

        RedisUserDeviceKey rUdk=new RedisUserDeviceKey(udk);
        RedisOperService roService=new RedisOperService(redisConn, 2);
        try {
            //从Redis中获得对应UserId，并判断是否
            String _value=roService.get(rUdk.getKey_SysDevice_User());
            String _userId=(_value==null?null:new String(_value));
            if (!StringUtils.isNullOrEmptyOrSpace(_userId)) _userId=_userId.substring(_userId.indexOf('_')+1);
            boolean hadLogon=(_userId==null?false:(_userId.equals(rUdk.getUserId())&&roService.get(rUdk.getKey_UserLogin_Status())!=null));

            if (hadLogon) {//已经登录
                Map<String, Object> param=new HashMap<String, Object>();
                param.put("UserId", _userId);
                GetUser gu=new GetUser(param);
                gu.setExpireTime(gConfig.getLoginsession());
                gu.setRedisConn(redisConn);
                gu.setUserService(userService);
                UserPo uPo=gu.getBizData();
                if (uPo==null) {
                    //删除所有用户相关的Key值
                    delUserAll(rUdk, roService);
                    map.put("ReturnType", "1002");
                    map.put("Message", "不能找到相应的用户");
                    return map;
                }
                roService.set(rUdk.getKey_UserLogin_Status(), operDesc);
                roService.pExpire(rUdk.getKey_UserLogin_Status(), gConfig.getLoginsession());
                roService.pExpire(rUdk.getKey_SysDevice_User(), gConfig.getLoginsession());
                roService.pExpire(rUdk.getKey_UserSysDType_DId(), gConfig.getLoginsession());

                map.put("ReturnType", "1001");
                map.put("UserId", rUdk.getUserId());
                map.put("UserInfo", uPo.toViewMap());
                map.put("UDK", udk);
                map.put("Message", "用户已登录");
            } else {//若在Redis中未发现已登录
                //从持久化存储中获取登录情况
                UserLoginPo ulPo=null;
                try {
                    ulPo=ulService.getUserUsedInDevice(udk.getSysWith().getOwnerType(), udk.getSysWith().getOwnerId(), udk.getDevice().getDeviceType().getType(), udk.getDevice().getDeviceId());
                } catch(Exception e) {}
                boolean noLog=false;
                noLog=(ulPo==null||ulPo.getStatus()!=1||ulPo.getUserId()==null);
                if (noLog) {//无法登录
                    //删除在该设备上的登录信息
                    if (!StringUtils.isNullOrEmptyOrSpace(_userId)) {
                        rUdk.setUserId(_userId);
                        cleanOneSession(rUdk, roService);
                    }
                    map.put("ReturnType", "0000");
                    map.put("Message", "请先登录");
                } else {//可以进行登录
                    if (udk.getUserId()!=null&&!udk.getUserId().equals(ulPo.getUserId())) {
                        cleanOneSession(rUdk, roService);
                        map.put("ReturnType", "1005");
                        map.put("Message", "请求用户与已登录用户不相符合");
                    } else {
                        RedisUserDeviceKey _oldKey=null;
                        //1-删除该用户在此类设备上的登录信息——踢出（不允许同一用户在同一类型的不同设备上同时登录）
                        try {
                            String did=roService.get(rUdk.getKey_UserSysDType_DId());
                            if (did!=null&&did.trim().equals(rUdk.getDevice().getDeviceId())) {
                                _oldKey=new RedisUserDeviceKey(udk);
                                _oldKey.getDevice().setDeviceId(did);
                                cleanOneSession(_oldKey, roService);
                            }
                        } catch(Exception e) {}
                        //2-删除之前的用户在该设备的登录信息（不允许同一设备上有两个用户同时登录）
                        try {
                            String uid=roService.get(rUdk.getKey_SysDevice_User());
                            uid=uid.substring(_userId.indexOf('_')+1);
                            if (uid!=null&&uid.trim().length()>0) {
                                _oldKey=new RedisUserDeviceKey(udk);
                                _oldKey.setUserId(uid);
                                cleanOneSession(_oldKey, roService);
                            }
                        } catch(Exception e) { }

                        rUdk.setUserId(ulPo.getUserId());
                        udk.setUserId(ulPo.getUserId());
                        addOneSession(rUdk, roService, operDesc);

                        Map<String, Object> param=new HashMap<String, Object>();
                        param.put("UserId", ulPo.getUserId());
                        GetUser gu=new GetUser(param);
                        gu.setExpireTime(gConfig.getLoginsession());
                        gu.setRedisConn(redisConn);
                        gu.setUserService(userService);
                        UserPo uPo=gu.getBizData();
                        if (uPo==null) {
                            //删除所有用户相关的Key值
                            delUserAll(rUdk, roService);
                            map.put("ReturnType", "1002");
                            map.put("Message", "不能找到相应的用户");
                            return map;
                        }
                        map.put("ReturnType", "1001");
                        map.put("UserId", rUdk.getUserId());
                        map.put("UserInfo", uPo.toViewMap());
                        map.put("UDK", udk);
                        map.put("Message", "用户已登录");
                    }
                }
            }
        } finally {
            if (roService!=null) roService.close();
            roService=null;
        }
        return map;
    }

    @Override
    public void registUser(UserDeviceKey udk, String apiName) {
        RedisUserDeviceKey rUdk=new RedisUserDeviceKey(udk);
        RedisOperService roService=new RedisOperService(redisConn, 2);
        addOneSession(rUdk, roService, apiName);
    }

    private Map<String, Object> checkUdk(UserDeviceKey udk) {
        Map<String,Object> map=new HashMap<String, Object>();
        if (udk==null) {
            map.put("ReturnType", "2000");
            map.put("Message", "用户设备key为空，无法处理");
            return map;
        }
        DeviceType dt=udk.getDevice().getDeviceType();
        if (dt==DeviceType.ERR) {
            map.put("ReturnType", "2101");
            map.put("Message", "设备类型不合法");
            return map;
        }
        if (StringUtils.isNullOrEmptyOrSpace(udk.getDevice().getDeviceId())) {
            if (dt==DeviceType.PC) {
                map.put("ReturnType", "2102");
                map.put("Message", "未获得SessionId无法处理");
            } else {
                map.put("ReturnType", "2103");
                map.put("Message", "未获得IMEI无法处理");
            }
            return map;
        }
        return null;
    }

    private void addOneSession(RedisUserDeviceKey rUdk, RedisOperService ros, String operDesc) {
        ros.rPush(rUdk.getKey_UserList(), rUdk.getKey_UserLogin_Status(), rUdk.getKey_SysDevice_User(), rUdk.getKey_UserSysDType_DId());
        ros.set(rUdk.getKey_UserLogin_Status(), operDesc);
        ros.pExpire(rUdk.getKey_UserLogin_Status(), gConfig.getLoginsession());
        ros.set(rUdk.getKey_SysDevice_User(), rUdk.getUserStr());
        ros.pExpire(rUdk.getKey_SysDevice_User(), gConfig.getLoginsession());
        ros.set(rUdk.getKey_UserSysDType_DId(), rUdk.getDevice().getDeviceId());
        ros.pExpire(rUdk.getKey_UserSysDType_DId(), gConfig.getLoginsession());
    }

    private void cleanOneSession(RedisUserDeviceKey rUdk, RedisOperService ros) {
        delUserList(rUdk, ros, rUdk.getKey_UserLogin_Status(), rUdk.getKey_SysDevice_User(), rUdk.getKey_UserSysDType_DId());
        ros.del(rUdk.getKey_UserLogin_Status(), rUdk.getKey_SysDevice_User(), rUdk.getKey_UserSysDType_DId());
    }

    //==以下两个函数是处理
    private void delUserAll(RedisUserDeviceKey udk, RedisOperService ros) {
        if (udk==null) return ;
        String listKey=udk.getKey_UserList();
        List<String> userKeyList=ros.lRange(listKey, 0, -1);
        if (userKeyList!=null&&!userKeyList.isEmpty()) {
            for (String userKey: userKeyList) ros.del(userKey);
        }
        ros.del(listKey);
    }
    private void delUserList(RedisUserDeviceKey udk, RedisOperService ros, String... key) {
        if (udk==null) return ;
        String listKey=udk.getKey_UserList();
        List<String> userKeyList=ros.lRange(listKey, 0, -1);
        for (String ok: key) {
            if (userKeyList!=null&&!userKeyList.isEmpty()) {
                for (String userKey: userKeyList) {
                    if (userKey.equals(key)) {
                        ros.lRemove(listKey, ok);
                    }
                }
            }
        }
    }
}