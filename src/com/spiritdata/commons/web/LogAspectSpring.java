package com.spiritdata.commons.web;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.spiritdata.commons.logvisit.LogVisitUtils;
import com.spiritdata.commons.logvisit.mem.LogVisitMemory;
import com.spiritdata.commons.logvisit.persis.pojo.LogVisitPo;
import com.spiritdata.commons.model.Owner;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.RequestUtils;
import com.spiritdata.prgconf.ConfigGroupConstants;

/**
 * 所有网络业务请求的切面。
 * 注意，这里只处理visitType=2的数据请求。
 * @author wanghui
 */
@Aspect
@Component
public class LogAspectSpring {
    @Pointcut("execution(public * com.spiritdata.**.web.*Controller.*(..))")  
    private void controllerLog(){}//定义一个切入点，所有控制类的

    /**
     * 在正常调用方法后。完成
     */
    @After("controllerLog()") 
    public void afterDoing() {
        RequestAttributes ra=RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra=(ServletRequestAttributes) ra;
        HttpServletRequest request=sra.getRequest();
        //存储日志
        LogVisitPo lvOp=(LogVisitPo)request.getAttribute("lvObj");
        if (lvOp!=null) {
            lvOp.setEndTime(new Timestamp(System.currentTimeMillis()));
            try {LogVisitMemory.getInstance().put2Queue(lvOp);} catch(Exception e) {};
        }
    }

    @Around("controllerLog()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        //0-获得Request
        RequestAttributes ra=RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra=(ServletRequestAttributes) ra;
        HttpServletRequest request=sra.getRequest();
        //1-获取参数
        Map<String, Object> m=RequestUtils.getDataFromRequest(request);
        request.setAttribute("mergedParam", m);
        //2-准备日志数据
        LogVisitPo lvOp=LogVisitUtils.buildApiLogDataFromRequest(request);
        if (m!=null) lvOp.setReqParam(JsonUtils.objToJson(m));
        lvOp.setVisitType(2);//在这里拦截的都是业务请求，即都是数据请求，要统一设置为2
        Owner servSys=(Owner)SystemCache.getCache(ConfigGroupConstants.GLOBAL_CONF).getContent();
        lvOp.setServSysType(servSys.getOwnerType());
        lvOp.setServSysId(servSys.getOwnerId());
        request.setAttribute("lvObj", lvOp);

        Signature signature=pjp.getSignature();
        MethodSignature methodSignature=(MethodSignature)signature;
        Method targetMethod=methodSignature.getMethod();
            
        Class clazz=targetMethod.getClass();
        Object result=pjp.proceed();
        return result;
    }
}