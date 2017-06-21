package com.spiritdata.commons.logvisit;

import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.spiritdata.commons.logvisit.persis.po.LogVisitPo;
import com.spiritdata.framework.util.SequenceUUID;
import com.spiritdata.framework.util.StringUtils;

public abstract class LogVisitUtils {
    /**
     * 以Request对象为基础，构建日志对象
     * @param request request对象
     * @return 日志对象
     */
    public static LogVisitPo buildLogDataFromRequest(HttpServletRequest request) {
        LogVisitPo lvPo=new LogVisitPo();
        lvPo.setId(SequenceUUID.getPureUUID());
        lvPo.setMethod(request.getMethod());
        String tempStr=request.getQueryString();
        tempStr=request.getRequestURL()+(StringUtils.isNullOrEmptyOrSpace(tempStr)?"":"?"+tempStr);
        lvPo.setReqUrl(tempStr);
        lvPo.setBeginTime(new Timestamp(System.currentTimeMillis()));
        return lvPo;
    }

    /**
     * 以参数为基础，为日志对象填入信息。注意这里不处理用户信息，用户信息在判断登录的逻辑中处理。
     * @param lvOp 被填入信息的日志对象
     * @param m 参数数据
     * @return 返回值，给出参数的错误的信息，若参数正确，返回null
     */
    public static String fillLogDataFromParam(LogVisitPo lvOp, Map<String, Object> m) {
        if (m==null) return "无法获取系统类型和标识";
        if (lvOp.getVisitType()==2) {//是接口访问类型
            String tempStr=null;
            int tempInt=0;
            //1-消费系统类型
            tempStr=m.get("SysType")==null?null:m.get("SysType")+"";
            if (StringUtils.isNullOrEmptyOrSpace(tempStr)) return "无法获取系统类型";
            lvOp.setVisitSysType(tempStr);
            //2-消费系统标识
            tempStr=m.get("SysId")==null?null:m.get("SysId")+"";
            if (StringUtils.isNullOrEmptyOrSpace(tempStr)) return "无法获取系统标识";
            lvOp.setVisitSysId(tempStr);
            //3-消费系统从自己的那个模块访问的此接口
            tempStr=m.get("SysModuleId")==null?null:m.get("SysModuleId")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setVisitModuleId(tempStr);
            //--用户信息不在这里处理
            //4-设备类型
            tempInt=3;//默认是PC机器
            tempStr=m.get("DeviceType")==null?null:m.get("DeviceType")+"";
            try { tempInt=Integer.parseInt(tempStr); } catch(Exception e) {};
            lvOp.setDeviceType(tempInt);
            //5-设备Id
            tempStr=m.get("DeviceId")==null?null:m.get("DeviceId")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setDeviceId(tempStr);
            //6-设备型号
            tempStr=m.get("DeviceClass")==null?null:m.get("DeviceClass")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setDeviceClass(tempStr);
            //7-设备屏幕
            tempStr=m.get("ScreenSize")==null?null:m.get("ScreenSize")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setScreenSize(tempStr);
            //8-客户端Ip
            tempStr=m.get("ClientIp")==null?null:m.get("ClientIp")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setClientIp(tempStr);
            //9-地理坐标
            tempStr=m.get("PointInfo")==null?null:m.get("PointInfo")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setPointInfo(tempStr);
            //10-浏览器名称
            tempStr=m.get("ExploreName")==null?null:m.get("ExploreName")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setExploreName(tempStr);
            //11-浏览器版本
            tempStr=m.get("ExploreVer")==null?null:m.get("ExploreVer")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setExploreVer(tempStr);
            //12-操作系统名称
            tempStr=m.get("OsName")==null?null:m.get("OsName")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setOsName(tempStr);
            //13-操作系统版本
            tempStr=m.get("OsVer")==null?null:m.get("OsVer")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setOsVer(tempStr);
            //--Api名称不在这里处理
            //14-从那个URL访问的，可空
            tempStr=m.get("FromUrl")==null?null:m.get("FromUrl")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setFromUrl(tempStr);
            //15-主对象类型
            tempStr=m.get("ObjType")==null?null:m.get("ObjType")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setObjType(tempStr);
            //16-对象Id
            tempStr=m.get("ObjId")==null?null:m.get("ObjId")+"";
            if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) lvOp.setObjId(tempStr);
        }
        return null;
    }
}