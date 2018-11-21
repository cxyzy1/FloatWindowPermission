/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.android.floatwindowpermission;

import android.content.Context;

import com.cxyzy.tools.permissions.floatwindow.BaseFloatWindowManager;

/**
 * Description:
 *
 * @author zhaozp
 * @since 2016-10-17
 */

public class FloatWindowManager extends BaseFloatWindowManager {
    private static final String TAG = FloatWindowManager.class.getSimpleName();

    private static volatile FloatWindowManager instance;

    private AVCallFloatView floatView = null;

    public static FloatWindowManager getInstance() {
        if (instance == null) {
            synchronized (FloatWindowManager.class) {
                if (instance == null) {
                    instance = new FloatWindowManager();
                }
            }
        }
        return instance;
    }

    @Override
    protected void showFloatView(Context context) {
        floatView = new AVCallFloatView(context);
        floatView.setParams(mParams);
        floatView.setIsShowing(true);
        windowManager.addView(floatView, mParams);
    }

    @Override
    public void dismissWindow() {
        super.dismissWindow();
        floatView.setIsShowing(false);
        if (windowManager != null && floatView != null) {
            windowManager.removeViewImmediate(floatView);
        }
    }

}
