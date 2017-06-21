package com.spiritdata.commons.model;

/**
 * 设备信息，是对客户端设备的一个简单描述。仅包含deviceType,deviceId
 * <pre>
 * =================================================
 *     deviceType         deviceId
 * -------------------------------------------------
 *   =0服务器,系统      系统分类+::+系统编号，可参考owner
 *   =1手机             手机IMEI
 *   =2设备             设备IMEI
 *   =3PC机             可能收集不到，计算机CPU号或网卡Mac地址
 * =================================================
 * </pre>
 * @author wanghui
 */
public class Device {
    private DeviceType deviceType; //设备分类
    private String deviceId; //设备编号

    public Device() {
        super();
    }
    public Device(DeviceType deviceType, String deviceId) {
        super();
        this.deviceType=deviceType;
        this.deviceId=deviceId;
    }
    public Device(int deviceType, String deviceId) {
        super();
        this.deviceType=DeviceType.buildDtByType(deviceType);
        this.deviceId=deviceId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }
    public int getDeviceTypeInt() {
        return deviceType.getType();
    }
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType=deviceType;
    }
    public void setDeviceType(int deviceType) {
        this.deviceType=DeviceType.buildDtByType(deviceType);
    }
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId=deviceId;
    }

    /**
     * 得到标识字符串
     * @return 标识字符串
     */
    @Override
    public String toString() {
        return this.deviceType.getType()+"_"+this.deviceId;
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