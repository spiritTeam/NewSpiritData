package com.spiritdata.passport.session.redis;

import com.spiritdata.commons.model.DeviceType;
import com.spiritdata.commons.model.UserDeviceKey;
import com.spiritdata.framework.util.StringUtils;

/**
 * Redis设备用户系统key
 * @author wanghui *
 */
public class RedisUserDeviceKey extends UserDeviceKey implements RedisLoginData {
    private static final long serialVersionUID = 2017041361668506482L;

    /**
     * 构造Key值，通过一个一般的设备用户系统Key
     * @param udKey 设备用户系统Key
     */
    public RedisUserDeviceKey(UserDeviceKey udKey) {
        if (udKey==null) throw new IllegalArgumentException("设备用户系统Key不能为空");
        if (udKey.getDevice().getDeviceType()==DeviceType.ERR)  new IllegalArgumentException("设备类型不合法");

        this.setUserId(udKey.getUserId());
        this.setDevice(udKey.getDevice());
        this.setSysWith(udKey.getSysWith());
    }

    @Override
    public String getKey_Lock() {
        if (StringUtils.isNullOrEmptyOrSpace(this.getUserId())&&StringUtils.isNullOrEmptyOrSpace(this.getDevice().getDeviceId())) {
            throw new RuntimeException("用户Id和设备Id不能同时为空，无法生成key");
        }
        String ret="Session_User_LoginLock=UserId_DeviceId="+this.getUserId()+"_"+this.getDevice().getDeviceId();
        return ret;
    }

    @Override
    public String getKey_UserLoginStatus() {
        if (StringUtils.isNullOrEmptyOrSpace(this.getSysWith().getOwnerId())) {
            throw new RuntimeException("用户所用系统标识不能为空，无法生成key");
        }
        return "Session_User_Login=UserId_DType_DId_SType_SId=="+this.getUserId()+"_"+this.getDevice().getDeviceType()+"_"+this.getDevice().getDeviceId()+"_"+this.getSysWith().getOwnerType()+"_"+this.getSysWith().getOwnerId();
    }

    @Override
    public String getKey_DeviceSys_UserId() {
        if (StringUtils.isNullOrEmptyOrSpace(this.getDevice().getDeviceId())||StringUtils.isNullOrEmptyOrSpace(this.getSysWith().getOwnerId())) {
            throw new RuntimeException("设备Id或系统标识不为空");
        }
        return "Session_LoginDeviceSys_UserId=DType_DId_SType_SId="+this.getDevice().getDeviceType()+"_"+this.getDevice().getDeviceId()+"_"+this.getSysWith().getOwnerType()+"_"+this.getSysWith().getOwnerId();
    }
}