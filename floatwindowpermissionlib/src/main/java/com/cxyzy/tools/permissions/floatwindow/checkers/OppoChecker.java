package com.cxyzy.tools.permissions.floatwindow.checkers;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.util.Log;

import com.cxyzy.tools.permissions.floatwindow.FloatWinPermissionUtil;

import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author Shawn_Dut
 * @since 2018-02-01
 */

/**
 * oppo rom悬浮窗权限检测类(适用于API 23以下)
 */
public class OppoChecker extends BaseChecker {

    private static final String TAG = "OppoChecker";

    @Override
    public boolean shouldCheckByMe() {
        //https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        return isThisRom() && Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    private boolean isThisRom() {
        return Build.MANUFACTURER.contains("OPPO") || Build.MANUFACTURER.contains("oppo");
    }

    /**
     * 检测 oppo 悬浮窗权限
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
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.KITKAT) {
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
     * oppo ROM 权限申请
     */
    @Override
    public void applyPermission(Context context) {
        //merge request from https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //com.coloros.safecenter/.sysfloatwindow.FloatWindowListActivity
            ComponentName comp = new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
