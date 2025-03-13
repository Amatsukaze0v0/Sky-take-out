package com.skytakeout.service;

import com.skytakeout.entity.User;

public interface UserService {
    /**
     * 微信登录
     * @param code
     * @return
     */
    User login(String code);
}
