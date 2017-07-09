package com.spiritdata.commons.checkcode.model;

import com.spiritdata.framework.util.StringUtils;

/**
 * 业务分类枚举值，需要验证码的业务功能有不同的编码
 * @author wanghui
 */
public enum BizCode {
    ERR("Err", "编码错误"),
    LOGIN("Login", "身份认证");

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

    public static BizCode buildByType(String _type) {
        if (StringUtils.isNullOrEmptyOrSpace(_type)) return ERR;
        if (_type.equals("Login")) return LOGIN;
        return ERR;
    }

    public static BizCode buildByName(String _name) {
        if (StringUtils.isNullOrEmptyOrSpace(_name)) return ERR;
        if (_name.equals("身份认证")) return LOGIN;
        return ERR;
    }
}