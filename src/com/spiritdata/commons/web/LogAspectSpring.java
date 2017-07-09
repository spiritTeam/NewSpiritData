package com.spiritdata.commons.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.spiritdata.anotation.ApiName;
import com.spiritdata.anotation.NeedLogin;
import com.spiritdata.commons.logvisit.LogVisitUtils;
import com.spiritdata.commons.logvisit.mem.LogVisitMemory;
import com.spiritdata.commons.logvisit.persis.po.LogVisitPo;
import com.spiritdata.commons.model.Owner;
import com.spiritdata.commons.model.UserDeviceKey;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.RequestUtils;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.passport.session.SessionService;

/**
 * 所有网络业务请求的切面。
 * 注意，这里只处理visitType=2的数据请求。
 * @author wanghui
 */
@Aspect
@Component
public class LogAspectSpring {
    @Resource(name="serverIdentify")
    private Owner _config_SI;
    @Resource(name="redisSessionService")
    private SessionService sessionService;

    @Pointcut("execution(public * com.spiritdata.**.web.*Controller.*(..))")  
    private void controllerLog(){}//定义一个切入点，所有控制类的

    /**
     * 环绕在实际方法前后的方法
     */
    @SuppressWarnings("rawtypes")
    @Around("controllerLog()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        //0-获得Request
        RequestAttributes ra=RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra=(ServletRequestAttributes)ra;
        HttpServletRequest request=sra.getRequest();
        //1-获取参数
        Map<String, Object> m=RequestUtils.getDataFromRequest(request);
        //2-准备日志数据
        LogVisitPo lvOp=LogVisitUtils.buildLogDataFromRequest(request);
        lvOp.setServSysType(_config_SI.getOwnerType()+"");
        lvOp.setServSysId(_config_SI.getOwnerId());
        lvOp.setVisitType(2);//在这里拦截的都是业务请求，即都是数据请求，要统一设置为2
        lvOp.setDealFlag(0);
        //2.1-根据获得的参数填写日志数据
        if (m!=null) lvOp.setReqParam(JsonUtils.objToJson(m));
        String errStr=LogVisitUtils.fillLogDataFromParam(lvOp, m);
        //2.2-访问人信息默认值的设置
        lvOp.setVisitorType("20012");
        lvOp.setVisitorId(request.getSession().getId());
        if (lvOp.getDeviceType()!=3) {//是设备
            if (!StringUtils.isNullOrEmptyOrSpace(lvOp.getDeviceId())) {
                lvOp.setVisitorType("20011");
                lvOp.setVisitorId(lvOp.getDeviceId());
            } else {
                if (StringUtils.isNullOrEmptyOrSpace(errStr)) errStr="无法获得设备Id";
                else errStr+=",无法获得设备Id";
            }
        }

        Object result=null;
        try {
            //3-处理注解：判断是否需要登录，获取Api名称
            boolean needLogin=false;
            Signature signature=pjp.getSignature();
            MethodSignature methodSignature=(MethodSignature)signature;
            Method targetMethod=methodSignature.getMethod();
            Annotation[] aArray=targetMethod.getAnnotations();
            String apiName=null;
            if (aArray!=null&&aArray.length>0) {
                int i=0;
                for (; i<aArray.length; i++) if (aArray[i] instanceof NeedLogin) break;
                needLogin=i<aArray.length;
                Annotation a=null;
                i=0;
                for (; i<aArray.length; i++) if (aArray[i] instanceof ApiName) break;
                if (i<aArray.length) a=aArray[i];
                if (a!=null) {
                    apiName=((ApiName)a).value();
                    if (StringUtils.isNullOrEmptyOrSpace(apiName)) {
                        if (lvOp.getVisitType()==2) {
                            if (StringUtils.isNullOrEmptyOrSpace(errStr)) errStr="无法获得Api名称";
                            else errStr+=",无法获得Api名称";
                        }
                    } else lvOp.setApiName(apiName);
                }
            }
            //4-若参数有问题
            if (!StringUtils.isNullOrEmptyOrSpace(errStr)) {
                Map<String, Object> rm=new HashMap<String, Object>();
                rm.put("ReturnType", "2099");
                rm.put("Message", errStr);
                lvOp.setDealFlag(2);//处理异常
                lvOp.setReturnData(JsonUtils.objToJson(rm));
                return rm;
            }
            //5-登录处理
            UserDeviceKey udk=new UserDeviceKey(m);
            if (lvOp.getDeviceType()==3) {
                lvOp.setDeviceId(request.getSession().getId());
                udk.setDevice(3, request.getSession().getId());
            }
            boolean logined=true;
            Map<String, Object> retMap=null;
            String errMsg=null;
            if (needLogin) {//判断是否登录了
                retMap=sessionService.dealUDkeyEntry(udk, apiName);
                if (retMap==null||retMap.get("ReturnType")==null) throw new Exception("判断是否登录成功方法出现未知错误");
                if ((retMap.get("ReturnType")+"").startsWith("2")) return retMap;
                if ("1001".equals(retMap.get("ReturnMap"))) {
                    udk=(UserDeviceKey)retMap.get("UDK");
                    if (retMap.get("UserId")!=null) request.setAttribute("UserId", retMap.get("UserId"));
                    if (retMap.get("UserInfo")!=null) request.setAttribute("UserInfo", retMap.get("UserInfo"));
                } else {
                    logined=false;
                    errMsg=retMap.get("Message")+"";
                }
            } else {//根据Session中登录的情况，重新写udk
                retMap=sessionService.getRealLoginUdk(udk);
                if (retMap==null||retMap.get("ReturnType")==null) throw new Exception("得到真实用户设备系统Key方法出现未知错误");
                if ((retMap.get("ReturnType")+"").startsWith("2")) {
                    lvOp.setReturnData(JsonUtils.objToJson(retMap));
                    return retMap;
                } else 
                if ("1001".equals(retMap.get("ReturnMap"))) {
                    udk=(UserDeviceKey)retMap.get("UDK");
                }
            }
            request.setAttribute("udKey", udk);
            if (logined) {//成功登录或根本不需要处理是否登录
                /**
                 * 调用实际的方法
                 */
                request.setAttribute("mergedParam", m);
                result=pjp.proceed();
            } else {//不成功登录
                Map<String, Object> rm=new HashMap<String, Object>();
                rm.put("ReturnType", "0000");
                rm.put("Message", "需要登录");
                if (!StringUtils.isNullOrEmptyOrSpace(errMsg)) rm.put("Message", errMsg);
                result=rm;
            }
            String oldUserId=udk.getUserId();
            udk=(UserDeviceKey)request.getAttribute("udKey");
            if (udk!=null&&udk.getUserId().equals(oldUserId)&&oldUserId==null) {
                lvOp.setVisitorType("20000");
                lvOp.setVisitorType(udk.getUserId());
            }
            lvOp.setDealFlag(2);//处理失败
            if ((result instanceof Map)&&((((Map)result).get("ReturnType")+"").equals("1001"))) {
                lvOp.setDealFlag(1);//处理成功
            }
        } catch(Exception e) {
            Map<String, Object> rm=new HashMap<String, Object>();
            rm.put("ReturnType", "T");
            rm.put("TClass", e.getClass().getName());
            rm.put("Message", e.getMessage());
            lvOp.setDealFlag(2);//处理异常
            result=rm;
        } finally {
            //记录日志，收集数据
            if (result!=null) lvOp.setReturnData(JsonUtils.objToJson(result));;
            lvOp.setEndTime(new Timestamp(System.currentTimeMillis()));
            try {
                LogVisitMemory.getInstance().put2Queue(lvOp);
            } catch (InterruptedException e) {}
        }
        return result;
    }
}