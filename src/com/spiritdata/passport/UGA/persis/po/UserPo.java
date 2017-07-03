package com.spiritdata.passport.UGA.persis.po;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;

/**
 * 用户类，是plat_User与plat_UserExt的联合表
 * @author wanghui
 */
public class UserPo extends UgaUser {
    private static final long serialVersionUID=400373609903981461L;

    private String idCard; //用户身份证号码
    private String mainPhoneNum; //用户主手机号码，用户可能有多个手机号码
    private String pubFields; //是否公开某些字段，是字段的名称
    private String descn; //用户描述

    public String getIdCard() {
        return idCard;
    }
    public void setIdCard(String idCard) {
        this.idCard=idCard;
    }

    public String getMainPhoneNum() {
        return mainPhoneNum;
    }
    public void setMainPhoneNum(String mainPhoneNum) {
        this.mainPhoneNum=mainPhoneNum;
    }

    public String getPubFields() {
        return pubFields;
    }
    public void setPubFields(String pubFields) {
        this.pubFields=pubFields;
    }

    public String getDescn() {
        return descn;
    }
    public void setDescn(String descn) {
        this.descn=descn;
    }

    public Map<String, Object> toViewMap() {
        Map<String, Object> retM=new HashMap<String, Object>();

        String _pubFields=this.pubFields==null?"":this.pubFields;
        if (!StringUtils.isNullOrEmptyOrSpace(this.userId)) retM.put("UserId", this.userId);
        if (_pubFields.indexOf("LoginName")!=-1) {
            if (!StringUtils.isNullOrEmptyOrSpace(this.loginName)) retM.put("LoginName", this.loginName);
        }
        if (_pubFields.indexOf("RealName")!=-1) {
            if (!StringUtils.isNullOrEmptyOrSpace(this.userName)) retM.put("RealName", this.userName);
        }
        if (_pubFields.indexOf("Desc")!=-1) {
            if (!StringUtils.isNullOrEmptyOrSpace(this.descn)) retM.put("Descn", this.descn);
        }
        if (this.CTime.getTime()>0) retM.put("CreateTime", this.CTime.getTime());
        if (this.lmTime.getTime()>0) retM.put("LastModifyTime", this.lmTime.getTime());

        return retM.isEmpty()?null:retM;
    }

    public Map<String, Object> toDetailMap() {
        Map<String, Object> retM=new HashMap<String, Object>();

        if (!StringUtils.isNullOrEmptyOrSpace(this.userId)) retM.put("UserId", this.userId);
        if (!StringUtils.isNullOrEmptyOrSpace(this.loginName)) retM.put("LoginName", this.loginName);
        if (!StringUtils.isNullOrEmptyOrSpace(this.password)) retM.put("Password", this.password);
        if (!StringUtils.isNullOrEmptyOrSpace(this.salt)) retM.put("Salt", this.salt);
        retM.put("Status", this.isValidate);
        if (!StringUtils.isNullOrEmptyOrSpace(this.userName)) retM.put("RealName", this.userName);
        if (!StringUtils.isNullOrEmptyOrSpace(this.idCard)) retM.put("IdCard", this.idCard);
        if (!StringUtils.isNullOrEmptyOrSpace(this.mainPhoneNum)) retM.put("PhoneNum", this.mainPhoneNum);
        if (!StringUtils.isNullOrEmptyOrSpace(this.pubFields)) retM.put("PubFields", this.pubFields);
        if (!StringUtils.isNullOrEmptyOrSpace(this.descn)) retM.put("Descn", this.descn);
        if (this.CTime.getTime()>0) retM.put("CreateTime", this.CTime.getTime());
        if (this.lmTime.getTime()>0) retM.put("LastModifyTime", this.lmTime.getTime());

        return retM.isEmpty()?null:retM;
    }

    /**
     * 从Json串，获得对象
     * @param s
     */
    @SuppressWarnings("unchecked")
    public void buildFromJson(String s) {
        Map<String, Object> m=(Map<String, Object>) JsonUtils.jsonToObj(s, Map.class);
        if (m==null||m.isEmpty()) return;

        String tempStr=null;
        int tempInt=0;
        long tempTime=0;
        //1-用户Id
        tempStr=m.get("UserId")==null?null:m.get("UserId")+"";
        if (StringUtils.isNullOrEmptyOrSpace(tempStr)) throw new RuntimeException("无法获得UserId");
        this.userId=tempStr; tempStr=null;
        //2-用户登录名
        tempStr=m.get("LoginName")==null?null:m.get("LoginName")+"";
        if (StringUtils.isNullOrEmptyOrSpace(tempStr)) throw new RuntimeException("无法获得LoginName");
        this.loginName=tempStr; tempStr=null;
        //3-用户密码
        tempStr=m.get("Password")==null?null:m.get("Password")+"";
        if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) this.password=tempStr; ;
        tempStr=null;
        //4-加盐
        tempStr=m.get("Salt")==null?null:m.get("Salt")+"";
        if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) this.salt=tempStr; ;
        tempStr=null;
        //5-是否生效
        tempInt=1;//默认生效
        tempStr=m.get("Status")==null?null:m.get("Status")+"";
        try { tempInt=Integer.parseInt(tempStr); } catch(Exception e) {};
        this.isValidate=tempInt;
        tempInt=0;
        //6-用户实名
        tempStr=m.get("RealName")==null?null:m.get("RealName")+"";
        if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) this.userName=tempStr; ;
        tempStr=null;
        //7-用户证件号：身份证
        tempStr=m.get("IdCard")==null?null:m.get("IdCard")+"";
        if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) this.idCard=tempStr; ;
        tempStr=null;
        //8-手机号码
        tempStr=m.get("PhoneNum")==null?null:m.get("PhoneNum")+"";
        if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) this.mainPhoneNum=tempStr; ;
        tempStr=null;
        //9-公布字段
        tempStr=m.get("PubFields")==null?null:m.get("PubFields")+"";
        if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) this.pubFields=tempStr; ;
        tempStr=null;
        //10-用户描述
        tempStr=m.get("Descn")==null?null:m.get("Descn")+"";
        if (!StringUtils.isNullOrEmptyOrSpace(tempStr)) this.descn=tempStr; ;
        tempStr=null;
        //11-创建时间
        tempStr=m.get("CreateTime")==null?null:m.get("CreateTime")+"";
        try { tempTime=Long.parseLong(tempStr); } catch(Exception e) {};
        this.CTime=new Timestamp(tempTime);
        tempTime=0l;
        //11-创建时间
        tempStr=m.get("LastModifyTime")==null?null:m.get("LastModifyTime")+"";
        try { tempTime=Long.parseLong(tempStr); } catch(Exception e) {};
        this.lmTime=new Timestamp(tempTime);
    }
}