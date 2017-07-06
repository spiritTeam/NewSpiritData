package com.spiritdata.commons.checkcode.model;

/**
 * 业务分类枚举值，需要验证码的业务功能有不同的编码
 * @author wanghui
 */
public enum BizCode {
    LOGIN("Login", "服务器");

    private BizCode(String value, String name) {
        _value=value;
        _name=name;
    }

    private String _value;
    private String _name;
    public String getCode() {
        return _value;
    }
    public String getName() {
        return _name;
    }
}