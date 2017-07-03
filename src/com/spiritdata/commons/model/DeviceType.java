package com.spiritdata.commons.model;

public enum DeviceType {
    SERVER(0, "服务器"),
    MOBILE(1, "手机"),
    MYDVC(2, "设备"),
    PC(3, "PC客户端"),
    ERR(-1, "错误客户端");

    private int _value;
    private String _name;
    private DeviceType(int value, String name) {
        _value=value;
        _name=name;
    }

    public static DeviceType buildDtByType(int _type) {
        if (_type==0) return SERVER;
        else if (_type==1) return MOBILE;
        else if (_type==2) return MYDVC;
        else if (_type==3) return PC;
        else return ERR;
    }

    public int getType() {
        return _value;
    }

    public String getName() {
        return _name;
    }
}