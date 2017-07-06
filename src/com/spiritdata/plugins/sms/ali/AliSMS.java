package com.spiritdata.plugins.sms.ali;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.framework.util.StringUtils;

public class AliSMS {

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product="Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain="dysmsapi.aliyuncs.com";

    private AliSmsConfig asConfig=null;
    private IAcsClient acsClient=null;

    public AliSMS(AliSmsConfig asConfig) {
        super();
        if (asConfig==null) throw new NullPointerException();

        this.asConfig=asConfig;
        //初始化acsClient,暂不支持region化
        IClientProfile profile=DefaultProfile.getProfile("cn-hangzhou", this.asConfig.getAccessKeyId(), this.asConfig.getAccessKeySecret());
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        this.acsClient=new DefaultAcsClient(profile);
    }

    public String sendSms(AliSmsMessage asMsg) {
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request=new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(asMsg.getPhoneNum());
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(StringUtils.isNullOrEmptyOrSpace(asMsg.getSignName())?this.asConfig.getSignName():asMsg.getSignName());
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(StringUtils.isNullOrEmptyOrSpace(asMsg.getTemplateCode())?this.asConfig.getTemplateCode():asMsg.getTemplateCode());
        //可选:模板中的变量替换JSON串
        request.setTemplateParam(asMsg.getParamJsonStr());
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("001001");
        try {
            SendSmsResponse sendSmsResponse=acsClient.getAcsResponse(request);
            return JsonUtils.objToJson(sendSmsResponse);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}