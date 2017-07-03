package com.spiritdata.passport.login.persis.po;

import java.sql.Timestamp;
import com.spiritdata.framework.core.model.BaseObject;

/**
 * 用户使用
 * @author wh
 */
public class UserLoginPo extends BaseObject {
    private static final long serialVersionUID=6647191780195842477L;

    private String ulId; //用户登录信息Id
    private String sysType; //登录系统类型
    private String sysId; //登录系统Id
    private String userId; //用户ID
    private int deviceType; //设备类型
    private String deviceId; //设备Id
    private int status; //状态：1-登录；2-注销；3-被踢出
    private long expire; //过期时间

    public String getUlId() {
        return ulId;
    }
    public void setUlId(String ulId) {
        this.ulId=ulId;
    }
    public String getSysType() {
        return sysType;
    }
    public void setSysType(String sysType) {
        this.sysType=sysType;
    }
    public String getSysId() {
        return sysId;
    }
    public void setSysId(String sysId) {
        this.sysId=sysId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId=userId;
    }
    public int getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(int deviceType) {
        this.deviceType=deviceType;
    }
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId=deviceId;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status=status;
    }
    public long getExpire() {
        return expire;
    }
    public void setExpire(long expire) {
        this.expire=expire;
    }
    public Timestamp getLmTime() {
        return lmTime;
    }
    public void setLmTime(Timestamp lmTime) {
        this.lmTime=lmTime;
    }
    private Timestamp lmTime; //最后修改时间:last modify time
}