/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.cxyzy.floatwindowpermission;

import android.content.Context;

/**
 * Description:
 *
 * @author zhaozp
 * @since 2016-10-17
 */

public class FloatWindowManager extends BaseFloatWindowManager {
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
        if (floatView != null) {
            floatView.setIsShowing(false);
            if (windowManager != null) {
                windowManager.removeViewImmediate(floatView);
            }
            floatView = null;
        }
    }

}
