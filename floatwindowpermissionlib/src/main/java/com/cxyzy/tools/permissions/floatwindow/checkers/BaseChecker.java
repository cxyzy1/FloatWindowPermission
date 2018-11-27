package com.cxyzy.tools.permissions.floatwindow.checkers;

import com.cxyzy.tools.permissions.floatwindow.FloatWinPermissionUtil;

public abstract class BaseChecker implements CheckerInterface {

    protected void checkFloatWindowPermission(boolean isPermitted, FloatWinPermissionUtil.CheckAndApplyPermissionCallback callback) {
        if (isPermitted) {
            callback.onPermitted();
        } else {
            callback.onNotPermitted();
        }
    }

}


