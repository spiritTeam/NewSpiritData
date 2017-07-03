package com.spiritdata.plugins.sms.ali;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.spiritdata.framework.util.JsonUtils;
import com.spiritdata.plugins.sms.Sms;

public class AliSMS implements Sms {
    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "LTAIwEVCGX8AG1RS";
    static final String accessKeySecret = "lpncnX2Wgu1yJedavpy05rs3jkgzJu";

    @Override
    public String sendSms(String phoneNum, String checkNum, String oper) {
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phoneNum);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("灵派诺达sms2017");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_75805052");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"mode\":\""+oper+"\", \"code\":\""+checkNum+"\"}");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("001001");

        try {
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            return JsonUtils.objToJson(sendSmsResponse);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}