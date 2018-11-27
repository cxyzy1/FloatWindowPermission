package com.cxyzy.tools.permissions.floatwindow.checkers;

import android.content.Context;

import com.cxyzy.tools.permissions.floatwindow.FloatWinPermissionUtil;

public interface CheckerInterface {
    /**
     * 检查是否是当前rom并且版本号是否对应
     */
    boolean shouldCheckByMe();

    /**
     * 检查是否有悬浮窗权限
     */
    void checkFloatWindowPermission(Context context, FloatWinPermissionUtil.CheckAndApplyPermissionCallback callback);

    /**
     * 申请悬浮窗权限
     */
    void applyPermission(Context context);
}
