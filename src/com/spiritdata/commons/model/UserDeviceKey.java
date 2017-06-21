package com.spiritdata.commons.model;

import java.io.Serializable;
import java.util.Map;

import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.framework.util.StringUtils;

/**
 * 用户设备系统Key信息。说明用户所属系统，用户所用设备，用户信息。
 * 若是未登录的用户，则用户信息可以为空
 * @author wanghui
 */
public class UserDeviceKey extends BaseObject implements Serializable {
    private static final long serialVersionUID=8784805045595806786L;

    private Device device;//用户所用设备
    private Owner sysWith;//用户所用系统
    private String userId;//用户信息

    public UserDeviceKey() {
        super();
    }

    /**
     * 从Map中构造一个用户设备系统Key
     * @param m 前端参数Map
     */
    public UserDeviceKey(Map<String, Object> m) {
        super();
        String tempStr=null;
        int tempInt=0;

        //1-用户所用系统
        int oType;
        String oId;
        //1.1系统类型
        tempStr=m.get("SysType")==null?null:m.get("SysType")+"";
        try { tempInt=Integer.parseInt(tempStr); } catch(Exception e) {};
        oType=tempInt;
        if (oType==0) throw new IllegalArgumentException("从Map中无法正确获得系统类型");
        //1.2系统标识
        tempStr=m.get("SysId")==null?null:m.get("SysId")+"";
        if (StringUtils.isNullOrEmptyOrSpace(tempStr)) throw new IllegalArgumentException("从Map中无法正确获得系统标识");
        oId=tempStr;
        this.sysWith=new Owner(oType, oId, null);

        //2-用户所用设备
        //2.1-设备类型
        tempInt=3;//默认是PC机器
        tempStr=m.get("DeviceType")==null?null:m.get("DeviceType")+"";
        try { tempInt=Integer.parseInt(tempStr); } catch(Exception e) {};
        if (DeviceType.buildDtByType(tempInt)==DeviceType.ERR) throw new IllegalArgumentException("从Map中无法正确获得设备类型");
        //2.2-设备Id
        if (tempInt!=3&&StringUtils.isNullOrEmptyOrSpace(tempStr)) throw new IllegalArgumentException("从Map中无法正确获得设备信息");
        tempStr=m.get("DeviceId")==null?null:m.get("DeviceId")+"";
        this.device=new Device(tempInt, tempStr);

        //3-用户ID
        this.userId=m.get("UserId")==null?null:m.get("UserId")+"";
    }

    public Device getDevice() {
        return device;
    }
    public void setDevice(Device device) {
        this.device = device;
    }
    public void setDevice(int deviceType, String deviceId) {
        this.device=new Device(deviceType, deviceId);
    }

    public Owner getSysWith() {
        return sysWith;
    }
    public void setSysWith(Owner sysWith) {
        this.sysWith = sysWith;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获得UserDeviceKey的标识字符串
     * @return 标识字符串
     */
    @Override
    public String toString() {
        return this.device+"::"+this.sysWith+"::"+this.userId;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj==null) return false;
        try {
            return obj.hashCode()==this.hashCode();
        } catch(Exception e) {}
        return false;
    }
}