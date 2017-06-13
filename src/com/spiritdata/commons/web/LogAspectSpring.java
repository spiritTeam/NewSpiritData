package com.spiritdata.commons.web;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.spiritdata.commons.logvisit.LogVisitUtils;
import com.spiritdata.commons.logvisit.persis.pojo.LogVisitPo;
import com.spiritdata.framework.util.RequestUtils;

/**
 * 所有网络业务请求的切面。
 * @author wanghui
 */
@Aspect
@Component
public class LogAspectSpring {
    private Logger logger=LoggerFactory.getLogger(LogAspectSpring.class); //日志

    @Pointcut("execution(public * com.spiritdata.**.web.*Controller.*(..))")  
    private void controllerLog(){}//定义一个切入点，所有控制类的

    /**
     * 在方法执行之前。获得参数，准备日志数据。
     */
    @Before("controllerLog()")
    public void  beforeDoing() {
        RequestAttributes ra=RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra=(ServletRequestAttributes) ra;
        HttpServletRequest request=sra.getRequest();

        //1-获取参数
        Map<String, Object> m=RequestUtils.getDataFromRequest(request);
        request.setAttribute("mergedParam", m);
        //2-准备日志数据
        LogVisitPo lvOp=LogVisitUtils.buildApiLogDataFromRequest(request);
    }

    @After("controllerLog()") 
    public void afterDoing() {
        RequestAttributes ra=RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra=(ServletRequestAttributes) ra;
        HttpServletRequest request=sra.getRequest();
        //存储日志
        System.out.println("之后::"+request.getAttribute("testBeforeDoing"));
    }

    @Around("controllerLog()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        //0-获得Request
        RequestAttributes ra=RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra=(ServletRequestAttributes) ra;
        HttpServletRequest request=sra.getRequest();
        //0-获取参数
        Map<String, Object> m=RequestUtils.getDataFromRequest(request);
        request.setAttribute("mergedParam", m);
        
        LogVisitPo lvOp=LogVisitUtils.buildApiLogDataFromRequest(request);
        lvOp.setApiName("4.2.3-content/getLoopImgs");
        lvOp.setObjType("001");//内容对象
        lvOp.setDealFlag(1);//处理成功
        lvOp.setOwnerType(201);
        lvOp.setOwnerId("--");

        String url=request.getRequestURL().toString();
        String method=request.getMethod();
        String uri=request.getRequestURI();
        String queryString=request.getQueryString();
        logger.info("请求开始, 各个参数, url: {}, method: {}, uri: {}, params: {}", url, method, uri, queryString);

        Signature signature=pjp.getSignature();
        MethodSignature methodSignature=(MethodSignature)signature;
        Method targetMethod=methodSignature.getMethod();
            
        Class clazz=targetMethod.getClass();
        Object result=pjp.proceed();
        return result;
    }
}