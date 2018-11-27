package com.cxyzy.tools.permissions.floatwindow.checkers;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.util.Log;

import com.cxyzy.tools.permissions.floatwindow.FloatWinPermissionUtil;

import java.lang.reflect.Method;

/**
 * 360 rom悬浮窗权限检测类(适用于API 23以下)
 */
public class QikuChecker extends BaseChecker {
    private static final String TAG = "QikuChecker";

    @Override
    public boolean shouldCheckByMe() {
        //fix issue https://github.com/zhaozepeng/FloatWindowPermission/issues/9
        return isThisRom() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    private boolean isThisRom() {
        return Build.MANUFACTURER.contains("QiKU")
                || Build.MANUFACTURER.contains("360");
    }

    /**
     * 检测 360 悬浮窗权限
     */
    @Override
    public void checkFloatWindowPermission(Context context, FloatWinPermissionUtil.CheckAndApplyPermissionCallback callback) {
        boolean isPermitted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isPermitted = checkOp(context, Build.VERSION_CODES.N);
        }
        checkFloatWindowPermission(isPermitted, callback);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e(TAG, "Below API " + Build.VERSION_CODES.KITKAT + " cannot invoke!");
        }
        return false;
    }

    /**
     * 去360权限申请页面
     */
    @Override
    public void applyPermission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "can't open permission page with particular name, please use " +
                        "\"adb shell dumpsys activity\" command and tell me the name of the float window permission page");
            }
        }
    }


    private static boolean isIntentAvailable(Intent intent, Context context) {
        if (intent == null) {
            return false;
        }
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }
}
