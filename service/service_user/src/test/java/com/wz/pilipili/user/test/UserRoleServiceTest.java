package com.wz.pilipili.user.test;

import com.wz.pilipili.entity.auth.UserRole;
import com.wz.pilipili.user.service.UserRoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class UserRoleServiceTest {

    @Autowired
    private UserRoleService userRoleService;

    @Test
    public void testGetUserRolesByUserId() {
        long userId = 23;
        List<UserRole> roleList = userRoleService.getUserRoleListByUserId(userId);
        for (UserRole userRole : roleList) {
            System.out.println(userRole);
        }
    }

    @Test
    public void testDate() {
        Date date = new Date();
        System.out.println("date = " + date);
    }
}
