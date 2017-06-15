package com.spiritdata.prgconf;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.spiritdata.commons.model.Owner;
import com.spiritdata.framework.jsonconf.JsonConfig;
import com.spiritdata.framework.util.StringUtils;

public class ConfigLoadUtils {
    private final static FelEngine fel=new FelEngineImpl();

    public static Owner getServerConfig(JsonConfig jc) {
        if (jc==null) return null;

        //生成默认值
        Owner o=new Owner();
        o.setOwnerType(10000);
        o.setOwnerId("spiritData");
        o.setOwnerName("灵派数据");

        int tmpInt=-1;
        String tmpStr="";

        try {
            tmpInt=(int)fel.eval(jc.getString("serverIdentify.serverType"));
        } catch(Exception e) {tmpInt=-1;}
        if (tmpInt!=-1) o.setOwnerType(tmpInt);

        try {
            tmpStr=jc.getString("serverIdentify.serverId");
        } catch(Exception e) {tmpStr="";}
        if (!StringUtils.isNullOrEmptyOrSpace(tmpStr)) o.setOwnerId(tmpStr);
        tmpStr="";

        try {
            tmpStr=jc.getString("serverIdentify.serverName");
        } catch(Exception e) {tmpStr="";}
        if (!StringUtils.isNullOrEmptyOrSpace(tmpStr)) o.setOwnerName(tmpStr);
        tmpStr="";

        return o;
    }
}