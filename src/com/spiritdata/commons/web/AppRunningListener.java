package com.spiritdata.commons.web;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spiritdata.commons.logvisit.LogVisitListener;
import com.spiritdata.commons.model.Owner;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.jsonconf.JsonConfig;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.prgconf.ConfigGroupConstants;
import com.spiritdata.prgconf.ConfigLoadUtils;

public class AppRunningListener implements ServletContextListener {
    private Logger logger=LoggerFactory.getLogger(this.getClass());

    @Override
    //初始化
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            //加载配置文件
            loadConfig();
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

    /*
     * 加载服务配置
     * @param configFileName 配置文件
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void loadConfig() throws IOException {
        JsonConfig jc=null;
        try {
            String configFileName=(SystemCache.getCache(FConstants.APPOSPATH)==null?"":((CacheEle<String>)(SystemCache.getCache(FConstants.APPOSPATH))).getContent());
            configFileName+="WEB-INF"+File.separator+"app.jconf";
            jc=new JsonConfig(configFileName);
            logger.info("配置文件信息={}", jc.getAllConfInfo());
        } catch(Exception e) {
            logger.info(StringUtils.getAllMessage(e));
            jc=null;
            e.printStackTrace();
        }
        if (jc!=null) {
            Owner servSys=ConfigLoadUtils.getServerConfig(jc);
            if (servSys!=null) SystemCache.setCache(new CacheEle<Owner>(ConfigGroupConstants.GLOBAL_CONF, "全局配置信息", servSys));
        }
    }
}