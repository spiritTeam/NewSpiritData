package com.spiritdata.plugins.sms.ali;

public class AliSmsMessage {
    private String signName; //短信签名
    private String templateCode; //短信模板编码
    private String phoneNum; //目的手机号码
    private String paramJsonStr; //模板中的变量替换JSON串
    public String getSignName() {
        return signName;
    }
    public void setSignName(String signName) {
        this.signName=signName;
    }
    public String getTemplateCode() {
        return templateCode;
    }
    public void setTemplateCode(String templateCode) {
        this.templateCode=templateCode;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum=phoneNum;
    }
    public String getParamJsonStr() {
        return paramJsonStr;
    }
    public void setParamJsonStr(String paramJsonStr) {
        this.paramJsonStr=paramJsonStr;
    }
}