package com.spiritdata.passport.UGA.service;

import com.spiritdata.framework.UGA.UgaUser;
import com.spiritdata.framework.UGA.UgaUserService;

public class UserService implements UgaUserService {

    @Override
    public <V extends UgaUser> V getUserByLoginName(String loginName) {
        return null;
    }

    @Override
    public <V extends UgaUser> V getUserById(String userId) {
        return null;
    }
}