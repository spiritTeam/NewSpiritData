package com.spiritdata.passport.session;

import java.util.Map;

import com.spiritdata.commons.model.UserDeviceKey;

/**
 * 与Session处理相关的方法
 * @author wanghui
 */
public interface SessionService {
    /**
     * 处理用户设备Key进入系统，若未登录要看之前的登录情况，自动登录
     * @param udk 根据用户设备Key
     * @param operDesc 操作的描述
     * @return 
     */
    public Map<String, Object> dealUDkeyEntry(UserDeviceKey udk, String operDesc);
    /**
     * 根据给定的用户系统信息，得到记录在系统中的真实登录用户的信息，并修改参数udk中的信息
     * @param udk 用户设备Key，若有真实登录用户，此参数也会变更为真实用户的新
     * @return 真正的用户信息
     */
    public Map<String, Object> getRealLoginUdk(UserDeviceKey udk);
}