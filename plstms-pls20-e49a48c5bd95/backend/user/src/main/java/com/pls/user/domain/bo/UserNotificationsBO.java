package com.pls.user.domain.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * BO for user notification.
 *
 * @author Nikita Cherevko
 */
public class UserNotificationsBO {
    private long userId;
    private String email;
    private List<String> notifications = new ArrayList<String>();

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
