package com.spiritdata.passport.login.service;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.passport.login.persis.po.UserLoginPo;

/**
 * 移动设备用户使用情况
 * @author wh
 */
public class UserLoginService {
    @Resource(name="defaultDAO")
    private MybatisDAO<UserLoginPo> ulDao;

    @PostConstruct
    public void initParam() {
        ulDao.setNamespace("USERLOGIN");
    }

    /**
     * 保存用户使用情况
     * @param mu
     */
    public void saveMobileUsed(UserLoginPo ul) {
        try {
            ul.setUlId(SequenceUUID.getUUIDSubSegment(4));
            ulDao.insert(ul);
        } catch(Exception e) {
            try {
                ulDao.update("updateByDeviceId", ul);
            } catch(Exception e1) {
                e1.printStackTrace();
            }
        }
        //把其他的用户使用情况设置为未登录
        if (ul.getStatus()==1) ulDao.update("adjustByDeviceId", ul);
    }

    /**
     * 根据手机串号，获取最后使用情况
     * @param imei 手机串号
     * @param PCDType 设备类型
     */
    public UserLoginPo getUserUsedInDevice(int sysType, String sysId, int deviceType, String deviceId) {
        Map<String, Object> paraM=new HashMap<String, Object>();
        paraM.put("deviceType", deviceType+"");
        paraM.put("deviceId", deviceId);
        paraM.put("sysType", sysType+"");
        paraM.put("sysId", sysId);
        return ulDao.getInfoObject("getUserUsedInDevice", paraM);
    }
}