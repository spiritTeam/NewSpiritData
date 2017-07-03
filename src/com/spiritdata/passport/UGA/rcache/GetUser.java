package com.spiritdata.passport.UGA.rcache;

import java.util.Map;
import java.lang.IllegalArgumentException;

import com.spiritdata.framework.ext.redis.RedisGetAndSet;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.passport.UGA.persis.po.UserPo;
import com.spiritdata.passport.UGA.service.UserService;

/**
 * 从缓存或持久化存储中获得用户信息。
 * @author wanghui
 *
 */
public class GetUser extends RedisGetAndSet<UserPo> {
    private String userId; //用户设备
    private UserService userService; //用户服务
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public GetUser(Map<String, Object> param) {
        super(param);
        userId=param.get("UserId")==null?"-1":(String)param.get("UserId");
    }

    @Override
    public UserPo getFromPersis() {
        return userService.getUserById(userId);
    }

    @Override
    protected String getKey() {
        if (StringUtils.isNullOrEmptyOrSpace(userId)) throw new IllegalArgumentException("用户Id没有设置");
        return "Cache_User_UserInfo::UId::"+userId;
    }

    @Override
    protected UserPo convert(String s) {
        if (StringUtils.isNullOrEmptyOrSpace(s)) return null;
        UserPo uPo=new UserPo();
        uPo.buildFromJson(s);
        return uPo;
    }

    @Override
    protected String convert(UserPo item) {
        if (item==null) return null;
        return JsonUtils.objToJson(item.toDetailMap());
    }
}