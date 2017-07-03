package com.spiritdata.commons.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.spiritdata.commons.logvisit.LogVisitListener;

public class AppRunningListener implements ServletContextListener {
    private Logger logger=LoggerFactory.getLogger(this.getClass());

    @Override
    //初始化
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            Thread.sleep(3000);//等待spring加载完成
            //启动数据收集数据
            LogVisitListener.begin();
        } catch(Exception e) {
            logger.error("Web运行时监听启动异常：",e);
        }
    }

    @Override
    //销毁
    public void contextDestroyed(ServletContextEvent arg0) {
    }
}