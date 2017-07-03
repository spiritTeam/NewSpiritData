package com.spiritdata.passport.session.redis;

/**
 * 用于Redis数据操作中，生成Key或相应的值。本接口仅为实现RedisSession框架中的数据获取。
 * RedisSession可被多平台实现
 * @author wanghui
 */
public interface RedisLoginData {
    /**
     * 得到用户登录某一系统的锁的key值——{LockLogin_UserSys_Radom::UType_UId_SType_SId::[]}
     * <p>其value是一随机数
     * @return 用户登录锁 key
     */
    public String getKey_LockLogin();

    /**
     * 得到“用户通过某设备登录某系统”的登录状态的key值——{Session_UserSysDvc_Status::UType_UId_SType_SId_DType_DId::[]}
     * <p>其value是：最近访问时间_访问方法（方法描述字符串）
     * @return 用户设备登录的key值
     */
    public String getKey_UserLogin_Status();

    /**
     * 得到某设备上登录某系统的用户Id的Key的值——{Session_SysDvc_User::SType_SId_DType_DId::[]}<br/>
     * <p>其value是：用户的ID
     * @return 对应的用户Id key
     */
    public String getKey_SysDevice_User();

    /**
     * 得到某类设备上登录某系统的用户的Key值——{Session_UserSysDType_Did::UType_UId_SType_SId_DType::[]}<br/>
     * <p>其value是：设备编号
     * @return 设备编号
     */
    public String getKey_UserSysDType_DId();
}