package com.pls.core.service.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Utility class to access spring resources in a static way. Try NOT to use it wherever it is possible.
 * 
 * @author Aleksandr Leshchenko
 */
@Component
@Lazy(false)
public class SpringApplicationContext implements InitializingBean {
    private static long adminUserId = 0L;

    @Value("${admin.personId}")
    private String adminId;

    @Override
    public void afterPropertiesSet() {
        adminUserId = Long.valueOf(adminId);
    }

    public static final Long getAdminUserId() {
        return adminUserId;
    }
}
