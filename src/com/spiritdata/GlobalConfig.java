package com.spiritdata;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;

/**
 * 系统全局性变量
 * @author wanghui
 */
public class GlobalConfig {
    private final static FelEngine fel=new FelEngineImpl();

    /**
     * 登录后Session的过期时间，默认值是30分钟
     */
    private int loginsession;

    public long getLoginsession() {
        return loginsession;
    }

    public void setLoginsession(String loginsession) {
        this.loginsession=30*60*1000;
        try {
            this.loginsession=(int)fel.eval(loginsession);
        } catch(Exception e) {}
    }
}