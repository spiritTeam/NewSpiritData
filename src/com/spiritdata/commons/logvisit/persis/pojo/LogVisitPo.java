package com.spiritdata.commons.logvisit.persis.pojo;

import java.sql.Timestamp;

import com.spiritdata.framework.core.model.BaseObject;

/**
 * 访问日志数据收集持久化对象，与数据库中log_Visit表对应
 * @author wh
 */
public class LogVisitPo extends BaseObject {
    private static final long serialVersionUID=-5047650691581766045L;

    private String id; //日志id
    //以下两项是一类Owner——服务系统
    private int servSysType; //服务系统类型——处理请求的服务 
    private String servSysId; //服务系统标识——处理请求的服务

    private int visitType; //访问类型：1=页面访问html；2=数据访问
    //以下两项是一类Owner——访问系统
    private int visitSysType; //访问系统类型——调用服务或访问服务的系统
    private String visitSysId; //访问系统标识——调用服务或访问服务的系统
    private String visitModelId; //访问系统模块——访问系统中那个模块调用的服务
    //以下两项是一类Owner——访问用户
    private int visitorType; //用户类型
    private String visitorId; //用户Id
    private String clientIp; //客户端Ip，可接受IPv6
    private String pointInfo; //可能是GPS坐标，以json格式记录
    private int deviceType; //1=手机2=设备3=PC
    private String deviceId; //设备编号：IMEI或PC标识（mac地址或CPU号）
    private String deviceClass; //设备型号：手机型号+品牌，如：HUAWEI, hc-10
    private String sreenSzie; //屏幕大小:json
    private String exploreName; //浏览器名称
    private String exploreVer; //浏览器版本
    private String osName; //操作系统名称
    private String osVer; //操作系统版本
    private String apiName; //访问名称,当是数据访问：访问Api名称，是服务器端的信息;当是html收集：是业务Title，是客户端信息;若是动作信息：则是客户端信息，是动作的表示，如播放
    private String fromUrl; //导入的URL, 当visitType=2无意义
    private String reqUrl; //请求的URL
    private String method; //请求方式是POST/GET/DEL等
    private String reqParam; //请求中的参数，形式为JSON
    private String objType; //主对象类型，是数据库模型中主对象的编号
    private String objId; //访问实体的ID
    private String dealFlag; //处理过程0正在处理1处理成功2处理失败
    private Timestamp beginTime; //开始处理时间，或操作点操作时间
    private Timestamp endTime; //结束处理时间

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id=id;
    }
    public int getServSysType() {
        return servSysType;
    }
    public void setServSysType(int servSysType) {
        this.servSysType=servSysType;
    }
    public String getServSysId() {
        return servSysId;
    }
    public void setServSysId(String servSysId) {
        this.servSysId=servSysId;
    }
    public int getVisitType() {
        return visitType;
    }
    public void setVisitType(int visitType) {
        this.visitType=visitType;
    }
    public int getVisitSysType() {
        return visitSysType;
    }
    public void setVisitSysType(int visitSysType) {
        this.visitSysType=visitSysType;
    }
    public String getVisitSysId() {
        return visitSysId;
    }
    public void setVisitSysId(String visitSysId) {
        this.visitSysId=visitSysId;
    }
    public String getVisitModelId() {
        return visitModelId;
    }
    public void setVisitModelId(String visitModelId) {
        this.visitModelId=visitModelId;
    }
    public int getVisitorType() {
        return visitorType;
    }
    public void setVisitorType(int visitorType) {
        this.visitorType=visitorType;
    }
    public String getVisitorId() {
        return visitorId;
    }
    public void setVisitorId(String visitorId) {
        this.visitorId=visitorId;
    }
    public String getClientIp() {
        return clientIp;
    }
    public void setClientIp(String clientIp) {
        this.clientIp=clientIp;
    }
    public String getPointInfo() {
        return pointInfo;
    }
    public void setPointInfo(String pointInfo) {
        this.pointInfo=pointInfo;
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
    public String getDeviceClass() {
        return deviceClass;
    }
    public void setDeviceClass(String deviceClass) {
        this.deviceClass=deviceClass;
    }
    public String getSreenSzie() {
        return sreenSzie;
    }
    public void setSreenSzie(String sreenSzie) {
        this.sreenSzie=sreenSzie;
    }
    public String getExploreName() {
        return exploreName;
    }
    public void setExploreName(String exploreName) {
        this.exploreName=exploreName;
    }
    public String getExploreVer() {
        return exploreVer;
    }
    public void setExploreVer(String exploreVer) {
        this.exploreVer=exploreVer;
    }
    public String getOsName() {
        return osName;
    }
    public void setOsName(String osName) {
        this.osName=osName;
    }
    public String getOsVer() {
        return osVer;
    }
    public void setOsVer(String osVer) {
        this.osVer=osVer;
    }
    public String getApiName() {
        return apiName;
    }
    public void setApiName(String apiName) {
        this.apiName=apiName;
    }
    public String getFromUrl() {
        return fromUrl;
    }
    public void setFromUrl(String fromUrl) {
        this.fromUrl=fromUrl;
    }
    public String getReqUrl() {
        return reqUrl;
    }
    public void setReqUrl(String reqUrl) {
        this.reqUrl=reqUrl;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method=method;
    }
    public String getReqParam() {
        return reqParam;
    }
    public void setReqParam(String reqParam) {
        this.reqParam=reqParam;
    }
    public String getObjType() {
        return objType;
    }
    public void setObjType(String objType) {
        this.objType=objType;
    }
    public String getObjId() {
        return objId;
    }
    public void setObjId(String objId) {
        this.objId=objId;
    }
    public String getDealFlag() {
        return dealFlag;
    }
    public void setDealFlag(String dealFlag) {
        this.dealFlag=dealFlag;
    }
    public Timestamp getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(Timestamp beginTime) {
        this.beginTime=beginTime;
    }
    public Timestamp getEndTime() {
        return endTime;
    }
    public void setEndTime(Timestamp endTime) {
        this.endTime=endTime;
    }
}