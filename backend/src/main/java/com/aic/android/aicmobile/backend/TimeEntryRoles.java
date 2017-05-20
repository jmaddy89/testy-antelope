package com.aic.android.aicmobile.backend;

/**
 * Created by JLM on 5/17/2017.
 */

public class TimeEntryRoles {

    private int roleId;
    private int userId;
    private String roleDesc;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }
}
