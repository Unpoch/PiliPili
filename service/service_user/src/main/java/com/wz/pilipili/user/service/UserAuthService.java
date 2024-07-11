package com.wz.pilipili.user.service;

import com.wz.pilipili.entity.auth.UserAuthorities;

public interface UserAuthService {
    UserAuthorities getUserAuthorities(Long userId);

    void addUserDefaultRole(Long id);
}
