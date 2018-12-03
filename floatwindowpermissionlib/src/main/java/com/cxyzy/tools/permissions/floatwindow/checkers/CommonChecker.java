package com.cxyzy.tools.permissions.floatwindow.checkers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.cxyzy.tools.permissions.floatwindow.FloatWinPermissionUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 通用rom悬浮窗权限检测类
 */
public class CommonChecker extends BaseChecker {
    private static final String TAG = "CommonChecker";


    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

    @Override
    public boolean shouldCheckByMe() {
        return true;
    }

    @Override
    public void checkFloatWindowPermission(Context context, FloatWinPermissionUtil.CheckAndApplyPermissionCallback callback) {
        boolean isPermitted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isPermitted = Settings.canDrawOverlays(context);
        }
        checkFloatWindowPermission(isPermitted, callback);
    }

    /**
     * 通用 rom 权限申请
     */
    @Override
    public void applyPermission(final Context context) {
        commonROMPermissionApplyInternal(context);
    }

    /**
     * 打开系统开启悬浮窗权限界面
     */
    public static void commonROMPermissionApplyInternal(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
