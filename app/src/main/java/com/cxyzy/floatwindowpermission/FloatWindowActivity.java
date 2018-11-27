/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.cxyzy.floatwindowpermission;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Description:
 *
 * @author zhaozp
 * @since 2016-10-17
 */

public class FloatWindowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.cxyzy.floatwindowpermission.R.layout.activity_main);
        findViewById(com.cxyzy.floatwindowpermission.R.id.btn_show_or_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindowManager.getInstance().applyOrShowFloatWindow(FloatWindowActivity.this);
            }
        });

        findViewById(com.cxyzy.floatwindowpermission.R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindowManager.getInstance().dismissWindow();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatWindowManager.getInstance().applyOrShowFloatWindow(FloatWindowActivity.this);
    }

}
