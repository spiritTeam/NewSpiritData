package com.spiritdata.plugins.sms;

/**
 * 短信接口
 * @author wanghui
 */
public interface Sms {

    /**
     * 发送短信
     * @param phoneNum 用户手机号码
     * @param checkNum 验证码
     * @param oper 相关操作
     * @return 返回码
     */
    public String sendSms(final String phoneNum, final String checkNum, final String oper);
}