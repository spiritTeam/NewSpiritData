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
    public String getKey_LockLogin() {
        if (this.getSysWith()==null) throw new RuntimeException("所用系统为空，无法生成key");
        if (StringUtils.isNullOrEmptyOrSpace(this.getSysWith().getOwnerId())) throw new RuntimeException("用户所用系统标识不能为空，无法生成key");
        return "LockLogin_UserSys_Radom::UType_UId_SType_SId::"+getUserStr()+"_"+this.getSysWith().getOwnerType()+"_"+this.getSysWith().getOwnerId();
    }

    @Override
    public String getKey_UserLogin_Status() {
        checkKey();
        return "Session_UserSysDvc_Status::UType_UId_SType_SId_DType_DId::"+this.getUserStr()+"_"+this.getSysWith().getOwnerType()+"_"+this.getSysWith().getOwnerId()+"_"+this.getDevice().toString();
    }

    @Override
    public String getKey_SysDevice_User() {
        checkKey();
        return "Session_SysDvc_User::SType_SId_DType_DId::"+this.getSysWith().getOwnerType()+"_"+this.getSysWith().getOwnerId()+"_"+this.getDevice().toString();
    }

    @Override
    public String getKey_UserSysDType_DId() {
        checkKey();
        return "Session_UserSysDType_Did::UType_UId_SType_SId_DType::"+this.getSysWith().getOwnerType()+"_"+this.getSysWith().getOwnerId()+"_"+getUserStr()+"_"+this.getDevice().getDeviceType().getType();
    }

    /**
     * 得到用户Session Key值列表的Key
     * @return
     */
    public String getKey_UserList() {
        return "Session_UserKey_List::UType_UId::"+getUserStr();
    }

    public String getUserStr() {
        if (StringUtils.isNullOrEmptyOrSpace(this.getUserId())&&StringUtils.isNullOrEmptyOrSpace(this.getDevice().getDeviceId())) {
            throw new RuntimeException("用户Id和设备Id不能同时为空，无法生成key");
        }
        String ret="";
        if (!StringUtils.isNullOrEmptyOrSpace(this.getUserId())) ret+="20000_"+this.getUserId();
        else {
            if (this.getDevice().getDeviceType()==DeviceType.PC) ret+="20012_"+this.getDevice().getDeviceId();
            else
            if (this.getDevice().getDeviceType()==DeviceType.MOBILE||this.getDevice().getDeviceType()==DeviceType.MYDVC) ret+="20011_"+this.getDevice().getDeviceId();
            else
            throw new RuntimeException("设备类型不合法，无法生成key");
        }
        return ret;
    }
    private void checkKey() {
        if (this.getDevice()==null) throw new RuntimeException("设备类型为空，无法生成key");
        if (this.getSysWith()==null) throw new RuntimeException("所用系统为空，无法生成key");
        if (StringUtils.isNullOrEmptyOrSpace(this.getSysWith().getOwnerId())) throw new RuntimeException("用户所用系统标识不能为空，无法生成key");
    }
}